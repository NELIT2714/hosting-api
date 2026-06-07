package dev.nelit.api.services.impl.vm;

import dev.nelit.api.domain.entity.vm.Vm;
import dev.nelit.api.domain.exception.vm.VmAlreadyActiveException;
import dev.nelit.api.domain.exception.vm.VmBlockedException;
import dev.nelit.api.domain.exception.vm.VmNotFoundException;
import dev.nelit.api.dto.response.*;
import dev.nelit.api.dto.response.VM.VmResponse;
import dev.nelit.api.dto.response.VM.VmStatusResponse;
import dev.nelit.api.grpc.VmManagerClient;
import dev.nelit.api.mappers.VMMapper;
import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.*;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.vm.VmService;
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
public class VmServiceImpl implements VmService {

    private final VmManagerClient vmManagerClient;
    private final VMMapper vmMapper;
    private final PlanService planService;
    private final NodeService nodeService;
    private final OsImageService osImageService;
    private final IpPoolService ipPoolService;
    private final VmRepository vmRepository;
    private final VpsOrderService vpsOrderService;
    private final TransactionalOperator tx;

    @Override
    public Flux<VmResponse> getAllByUserId(Long idUser) {
        return vmRepository.findAllByIdUser(idUser)
            .collectList()
            .flatMapMany(vms -> {
                List<Long> planIds = vms.stream().map(Vm::getIdPlan).distinct().toList();
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
    public Mono<VmResponse> getById(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> Mono.zip(
                    planService.getById(vm.getIdPlan()),
                    ipPoolService.getByIdVM(vm.getIdVM()).map(IpPoolResponse::ipAddress).defaultIfEmpty("N/A")
                )
                .map(tuple -> buildResponse(vm, tuple.getT1(), tuple.getT2()))
            );
    }

    @Override
    public Mono<VmResponse> getActiveVm(Long idVm, Long idUser) {
        return getById(idVm).flatMap(vm -> {
            if (!vm.idUser().equals(idUser)) return Mono.error(new VmNotFoundException());
            if (Boolean.FALSE.equals(vm.isActive())) return Mono.error(new VmNotFoundException());
            if (Boolean.TRUE.equals(vm.isBlocked())) return Mono.error(new VmBlockedException());
            return Mono.just(vm);
        });
    }

//    @Override
//    public Mono<VmResponse> setup(CreateVM vmDTO, Long idUser) {
//        return Mono.zip(
//                planService.getById(vmDTO.idPlan()),
//                osImageService.getById(vmDTO.idOsImage()),
//                nodeService.getAll().collectList()
//            )
//            .flatMap(tuple -> {
//                PlanResponse plan = tuple.getT1();
//                OsImageResponse osImage = tuple.getT2();
//                List<NodeResponse> nodes = tuple.getT3();
//
//                List<VmManager.NodeInfo> nodeInfos = buildNodeInfos(nodes);
//
//                return Mono.fromCallable(() -> vmManagerClient.pickNode(nodeInfos))
//                    .subscribeOn(Schedulers.boundedElastic())
//                    .flatMap(node -> ipPoolService.getFirstAvailable(node.getNodeId())
//                        .flatMap(ip -> create(idUser, plan.idPlan(), node.getNodeId(), ip)
//                            .flatMap(savedVM -> callGrpc(savedVM, plan, osImage, node, vmDTO.password(), vmDTO.sshKey()))
//                            .onErrorResume(e -> ipPoolService.unassign(ip.idIp()).then(Mono.error(e)))
//                        )
//                    );
//            }).map(vmMapper::toResponse);
//    }

    @Override
    public Mono<Vm> create(Long idUser, Long idPlan, Long idOsImage) {
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

    @Override
    public Mono<Void> start(Long idVm, Long idUser) {
        return getActiveVm(idVm, idUser).flatMap(vm -> nodeService.getById(vm.idNode()).flatMap(node -> {
            VmManager.NodeInfo nodeInfo = VmManager.NodeInfo.newBuilder()
                .setNodeId(node.idNode())
                .setIp(node.ipAddress())
                .setGrpcPort(node.grpcPort())
                .build();

            return Mono.fromCallable(() -> vmManagerClient.startVm(vm.uuid(), nodeInfo)).then();
        }));
    }

    @Override
    public Mono<Void> stop(Long idVm, Long idUser) {
        return getActiveVm(idVm, idUser).flatMap(vm -> nodeService.getById(vm.idNode()).flatMap(node -> {
            VmManager.NodeInfo nodeInfo = VmManager.NodeInfo.newBuilder()
                .setNodeId(node.idNode())
                .setIp(node.ipAddress())
                .setGrpcPort(node.grpcPort())
                .build();

            return Mono.fromCallable(() -> vmManagerClient.stopVm(vm.uuid(), nodeInfo)).then();
        }));
    }

    @Override
    public Mono<VmStatusResponse> getStatus(Long idVm, Long idUser) {
        return getActiveVm(idVm, idUser).flatMap(vm -> Mono.zip(planService.getById(vm.plan().idPlan()), nodeService.getById(vm.idNode()))
            .flatMap(tuple -> {
                PlanResponse plan = tuple.getT1();
                NodeResponse node = tuple.getT2();

                VmManager.NodeInfo nodeInfo = VmManager.NodeInfo.newBuilder()
                    .setNodeId(node.idNode())
                    .setIp(node.ipAddress())
                    .setGrpcPort(node.grpcPort())
                    .build();

                return Mono.fromCallable(() -> vmManagerClient.getStatus(vm.uuid(), nodeInfo))
                    .map(result -> VmStatusResponse.builder()
                        .vmName(vm.vmName())
                        .uuid(vm.uuid())
                        .status(result.getStatus())
                        .cpuPercent(result.getCpuPercent())
                        .memTotalMb(result.getMemTotalMb())
                        .memUsedMb(result.getMemUsedMb())
                        .memAvailableMb(result.getMemAvailableMb())
                        .plan(plan)
                        .ipAddress(vm.ipAddress())
                        .createdAt(vm.createdAt())
                        .expiresAt(vm.expiresAt())
                        .build()
                    );
            }));
    }

    @Override
    public Mono<Void> stopBySystem(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> nodeService.getById(vm.getIdNode())
                .flatMap(node -> {
                    VmManager.NodeInfo nodeInfo = VmManager.NodeInfo.newBuilder()
                        .setNodeId(node.idNode())
                        .setIp(node.ipAddress())
                        .setGrpcPort(node.grpcPort())
                        .build();
                    return Mono.fromCallable(() -> vmManagerClient.stopVm(vm.getUuid(), nodeInfo)).then();
                })
            );
    }

    @Override
    public Mono<Void> deleteBySystem(Long idVm) {
        return null;
    }

    @Override
    public Mono<Void> renew(Long idVm, Integer days) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> {
                Instant newExpiry = vm.getExpiresAt().plus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
                vm.setExpiresAt(newExpiry);
                return vmRepository.save(vm).then();
            });
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

    private Mono<Vm> create(Long idUser, Long planId, Long nodeId, IpPoolResponse ip) {
        Vm newVm = Vm.builder()
            .idUser(idUser)
            .idNode(nodeId)
            .idPlan(planId)
            .vmName(VMNameGenerator.generate("fi", 1))
            .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
            .build();

        return vmRepository.save(newVm)
            .flatMap(savedVm -> ipPoolService.assign(ip.idIp(), savedVm.getIdVM()).thenReturn(savedVm))
            .as(tx::transactional);
    }

    private Mono<Vm> callGrpc(Vm savedVm, PlanResponse plan, OsImageResponse osImage, VmManager.NodeInfo pickedNode, String password, String sshKey) {
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

    private VmResponse buildResponse(Vm vm, PlanResponse plan, String ipAddress) {
        return new VmResponse(vm.getIdVM(), vm.getVmName(), vm.getIdUser(), vm.getIdNode(), vm.getUuid(), ipAddress, vm.getIsActive(), vm.getIsBlocked(), vm.getCreatedAt(), vm.getExpiresAt(), plan);
    }
}