package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.VM;
import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.*;
import dev.nelit.api.grpc.VmManagerClient;
import dev.nelit.api.mappers.VMMapper;
import dev.nelit.api.repository.VMRepository;
import dev.nelit.api.services.*;
import dev.nelit.api.util.VMNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vm_manager.VmManager;

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
    private final TransactionalOperator tx;

    @Override
    public Mono<VMResponse> create(CreateVM vmDTO, Long idUser) {
        return Mono.zip(
            planService.getById(vmDTO.idPlan()),
            osImageService.getById(vmDTO.idOsImage()),
            nodeService.getAll().collectList()
        )
        .flatMap(tuple -> {
            PlanResponse plan = tuple.getT1();
            OsImageResponse osImage = tuple.getT2();
            List<NodeResponse> nodes = tuple.getT3();

            List<VmManager.NodeInfo> nodeInfos = nodes.stream()
                .map(node -> VmManager.NodeInfo.newBuilder()
                    .setNodeId(node.idNode())
                    .setIp(node.ipAddress())
                    .setGrpcPort(node.grpcPort())
                    .build()
                )
                .toList();

            return Mono.fromCallable(() -> vmManagerClient.pickNode(nodeInfos))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(node -> ipPoolService.getFirstAvailable(node.getNodeId())
                    .flatMap(ip -> saveVm(idUser, plan.idPlan(), node.getNodeId(), ip)
                        .flatMap(savedVM -> callGrpc(savedVM, plan, osImage, node, vmDTO.password(), vmDTO.sshKey()))
                        .onErrorResume(e -> ipPoolService.unassign(ip.idIp()).then(Mono.error(e)))
                    )
                );
        }).map(vmMapper::toResponse);
    }

    private Mono<VM> saveVm(Long idUser, Long planId, Long nodeId, IpPoolResponse ip) {
        VM newVm = VM.builder()
            .idUser(idUser)
            .idNode(nodeId)
            .idPlan(planId)
            .vmName(VMNameGenerator.generate("fi", 1))
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
}