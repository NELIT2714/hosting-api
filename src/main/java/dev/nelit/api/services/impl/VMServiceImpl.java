package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.VM;
import dev.nelit.api.domain.exception.vm.VmAlreadyActiveException;
import dev.nelit.api.domain.exception.vm.VmNotFoundException;
import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.*;
import dev.nelit.api.grpc.VmManagerClient;
import dev.nelit.api.mappers.VMMapper;
import dev.nelit.api.repository.VMRepository;
import dev.nelit.api.services.*;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.util.VMNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vm_manager.VmManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VMServiceImpl implements VMService {

    private final VmManagerClient vmManagerClient;
    private final VMMapper vmMapper;
    private final PlanService planService;
    private final NodeService nodeService;
    private final OsImageService osImageService;
    private final IpPoolService ipPoolService;
    private final VMRepository vmRepository;
    private final VpsOrderService vpsOrderService;
    private final TransactionalOperator tx;

    @Override
    public Flux<VMResponse> getAllByUserId(Long idUser) {
        return vmRepository.findAllByIdUser(idUser)
            .collectList()
            .flatMapMany(vms -> {
                List<Long> planIds = vms.stream().map(VM::getIdPlan).distinct().toList();
                return Flux.fromIterable(planIds)
                    .flatMap(planService::getById)
                    .collectMap(PlanResponse::idPlan)
                    .flatMapMany(plansMap -> Flux.fromIterable(vms)
                        .flatMap(vm -> ipPoolService.getByIdVM(vm.getIdVM())
                            .map(ip -> buildResponse(vm, plansMap.get(vm.getIdPlan()), ip.ipAddress()))
                            .defaultIfEmpty(buildResponse(vm, plansMap.get(vm.getIdPlan()), "N/A"))
                        )
                    );
            });
    }

    @Override
    public Mono<VMResponse> setup(CreateVM vmDTO, Long idUser) {
        return Mono.zip(
                planService.getById(vmDTO.idPlan()),
                osImageService.getById(vmDTO.idOsImage()),
                nodeService.getAll().collectList()
            )
            .flatMap(tuple -> {
                PlanResponse plan = tuple.getT1();
                OsImageResponse osImage = tuple.getT2();
                List<NodeResponse> nodes = tuple.getT3();

                List<VmManager.NodeInfo> nodeInfos = buildNodeInfos(nodes);

                return Mono.fromCallable(() -> vmManagerClient.pickNode(nodeInfos))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(node -> ipPoolService.getFirstAvailable(node.getNodeId())
                        .flatMap(ip -> create(idUser, plan.idPlan(), node.getNodeId(), ip)
                            .flatMap(savedVM -> callGrpc(savedVM, plan, osImage, node, vmDTO.password(), vmDTO.sshKey()))
                            .onErrorResume(e -> ipPoolService.unassign(ip.idIp()).then(Mono.error(e)))
                        )
                    );
            }).map(vmMapper::toResponse);
    }

    @Override
    public Mono<VM> create(Long idUser, Long idPlan, Long idOsImage) {
        return Mono.zip(
                planService.getById(idPlan),
                nodeService.getAll().collectList()
            )
            .flatMap(tuple -> {
                PlanResponse plan = tuple.getT1();
                List<NodeResponse> nodes = tuple.getT2();

                List<VmManager.NodeInfo> nodeInfos = buildNodeInfos(nodes);

                return Mono.fromCallable(() -> vmManagerClient.pickNode(nodeInfos))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(node -> ipPoolService.getFirstAvailable(node.getNodeId())
                        .flatMap(ip -> create(idUser, plan.idPlan(), node.getNodeId(), ip)
                            .onErrorResume(e -> ipPoolService.unassign(ip.idIp()).then(Mono.error(e)))
                        )
                    );
            });
    }

    @Override
    public Mono<Void> activate(Long idVm, Long idUser, String password, String sshKey) {
        return vpsOrderService.getByIdVm(idVm)
            .flatMap(order -> vmRepository.findById(idVm)
                .switchIfEmpty(Mono.error(new VmNotFoundException()))
                .flatMap(vm -> {
                    if (!vm.getIdUser().equals(idUser)) return Mono.error(new VmNotFoundException());
                    if (Boolean.TRUE.equals(vm.getIsActive())) return Mono.error(new VmAlreadyActiveException());
                    return Mono.zip(
                            planService.getById(vm.getIdPlan()),
                            osImageService.getById(order.getIdOsImage()),
                            nodeService.getById(vm.getIdNode())
                        )
                        .flatMap(tuple -> {
                            PlanResponse plan = tuple.getT1();
                            OsImageResponse osImage = tuple.getT2();
                            NodeResponse node = tuple.getT3();

                            VmManager.NodeInfo nodeInfo = VmManager.NodeInfo.newBuilder()
                                .setNodeId(node.idNode())
                                .setIp(node.ipAddress())
                                .setGrpcPort(node.grpcPort())
                                .build();

                            return callGrpc(vm, plan, osImage, nodeInfo, password, sshKey)
                                .flatMap(savedVm -> {
                                    savedVm.setIsActive(true);
                                    return vmRepository.save(savedVm);
                                });
                        });
                })
            )
            .then();
    }

    private List<VmManager.NodeInfo> buildNodeInfos(List<NodeResponse> nodes) {
        return nodes.stream()
            .map(node -> VmManager.NodeInfo.newBuilder()
                .setNodeId(node.idNode())
                .setIp(node.ipAddress())
                .setGrpcPort(node.grpcPort())
                .build()
            )
            .toList();
    }

    private Mono<VM> create(Long idUser, Long planId, Long nodeId, IpPoolResponse ip) {
        VM newVm = VM.builder()
            .idUser(idUser)
            .idNode(nodeId)
            .idPlan(planId)
            .vmName(VMNameGenerator.generate("fi", 1))
            .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .build();

        return vmRepository.save(newVm)
            .flatMap(savedVm -> ipPoolService.assign(ip.idIp(), savedVm.getIdVM()).thenReturn(savedVm))
            .as(tx::transactional);
    }

    private Mono<VM> callGrpc(VM savedVm, PlanResponse plan, OsImageResponse osImage, VmManager.NodeInfo pickedNode, String password, String sshKey) {
        return ipPoolService.getByIdVM(savedVm.getIdVM())
            .flatMap(ipPool -> Mono.fromCallable(() -> vmManagerClient.createVm(
                        savedVm.getVmName(),
                        plan.ramMb(),
                        plan.vcpus(),
                        plan.diskGb(),
                        osImage.fileName(),
                        ipPool.ipAddress(),
                        password,
                        sshKey,
                        pickedNode
                    ))
                    .subscribeOn(Schedulers.boundedElastic())
            )
            .flatMap(grpcResponse -> {
                savedVm.setUuid(grpcResponse.getUuid());
                return vmRepository.save(savedVm);
            });
    }

    private VMResponse buildResponse(VM vm, PlanResponse plan, String ipAddress) {
        return new VMResponse(vm.getIdVM(), vm.getVmName(), vm.getUuid(), ipAddress, vm.getIsActive(), vm.getCreatedAt(), vm.getExpiresAt(), plan);
    }
}