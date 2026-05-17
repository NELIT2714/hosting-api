package dev.nelit.api.services.impl;

import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.dto.response.VMResponse;
import dev.nelit.api.grpc.VmManagerClient;
import dev.nelit.api.mappers.VMMapper;
import dev.nelit.api.services.NodeService;
import dev.nelit.api.services.OsImageService;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.VMService;
import dev.nelit.api.util.VMNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    public Mono<VMResponse> create(CreateVM vmDTO) {
        return planService.getById(vmDTO.idPlan())
            .flatMap(plan -> osImageService.getById(vmDTO.idOsImage())
                .flatMap(osImage -> nodeService.getAll()
                    .flatMap(nodes -> {
                        List<VmManager.NodeInfo> nodeInfos = nodes.stream()
                            .map(node -> VmManager.NodeInfo.newBuilder()
                                .setNodeId(node.idNode())
                                .setIp(node.ipAddress())
                                .setGrpcPort(node.grpcPort())
                                .build()
                            )
                            .toList();

                        return Mono.fromCallable(() -> {
                                VmManager.NodeInfo pickedNode = vmManagerClient.pickNode(nodeInfos);

                                return vmManagerClient.createVm(
                                    VMNameGenerator.generate("fi", 1),
                                    plan.ramMb(),
                                    plan.vcpus(),
                                    plan.diskGb(),
                                    osImage.fileName(),
                                    pickedNode
                                );
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(vmMapper::toResponse);
                    })
                )
            );
    }
}