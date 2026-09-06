package dev.nelit.api.services.impl.vm;

import dev.nelit.api.domain.entity.vm.Vm;
import dev.nelit.api.domain.exception.vm.VmAlreadyActiveException;
import dev.nelit.api.domain.exception.vm.VmBlockedException;
import dev.nelit.api.domain.exception.vm.VmNotActiveException;
import dev.nelit.api.domain.exception.vm.VmNotFoundException;
import dev.nelit.api.dto.request.vm.ActivateVM;
import dev.nelit.api.dto.request.vm.ReinstallVM;
import dev.nelit.api.dto.response.IpPoolResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import dev.nelit.api.dto.response.OsImageResponse;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.dto.response.VM.VmResponse;
import dev.nelit.api.dto.response.VM.VmStatusResponse;
import dev.nelit.api.dto.response.VM.VncConsoleResponse;
import dev.nelit.api.grpc.VmManagerClient;
import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.IpPoolService;
import dev.nelit.api.services.NodeService;
import dev.nelit.api.services.OsImageService;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.vm.VmService;
import dev.nelit.api.util.VMNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vm_manager.VmManager;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class VmServiceImpl implements VmService {

    private final VmManagerClient vmManagerClient;
    private final PlanService planService;
    private final NodeService nodeService;
    private final OsImageService osImageService;
    private final IpPoolService ipPoolService;
    private final VmRepository vmRepository;
    private final VpsOrderService vpsOrderService;
    private final TransactionalOperator tx;
    private final ReactiveStringRedisTemplate redis;

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
    public Mono<Void> activate(Long idVm, Long idUser, ActivateVM request) {
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

                            vm.setIsActive(true);
                            return callGrpcCreate(vm, plan, osImage, toNodeInfo(node), request.password(), request.sshKey());
                        });
                })
            )
            .then();
    }

    @Override
    public Mono<Void> reinstall(Long idVm, Long idUser, ReinstallVM request) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> {
                if (!vm.getIdUser().equals(idUser)) return Mono.error(new VmNotFoundException());
                if (Boolean.FALSE.equals(vm.getIsActive())) return Mono.error(new VmNotActiveException());

                return Mono.zip(
                        osImageService.getById(request.idOsImage()),
                        nodeService.getById(vm.getIdNode())
                    )
                    .flatMap(tuple -> {
                        OsImageResponse osImage = tuple.getT1();
                        NodeResponse node = tuple.getT2();

                        return callGrpcReinstall(vm, osImage, toNodeInfo(node), request.password(), request.sshKey());
                    });
            })
            .then();
    }

    @Override
    public Mono<Void> start(Long idVm, Long idUser) {
        return executeVmAction(idVm, idUser, vmManagerClient::startVm);
    }

    @Override
    public Mono<Void> stop(Long idVm, Long idUser) {
        return executeVmAction(idVm, idUser, vmManagerClient::stopVm);
    }

    @Override
    public Mono<Void> restart(Long idVm, Long idUser) {
        return executeVmAction(idVm, idUser, vmManagerClient::restartVm);
    }

    @Override
    public Mono<VmStatusResponse> getStatus(Long idVm, Long idUser) {
        return getActiveVm(idVm, idUser).flatMap(vm -> Mono.zip(planService.getById(vm.plan().idPlan()), nodeService.getById(vm.idNode()))
            .flatMap(tuple -> {
                PlanResponse plan = tuple.getT1();
                NodeResponse node = tuple.getT2();

                return Mono.fromCallable(() -> vmManagerClient.getStatus(vm.uuid(), toNodeInfo(node)))
                    .map(result -> {
                        VmStatusResponse.DiskStats diskStats = result.hasDisk()
                            ? VmStatusResponse.DiskStats.builder()
                            .readMbS(result.getDisk().getReadMbS())
                            .writeMbS(result.getDisk().getWriteMbS())
                            .readIops(result.getDisk().getReadIops())
                            .writeIops(result.getDisk().getWriteIops())
                            .build()
                            : null;

                        return VmStatusResponse.builder()
                            .id(vm.idVm())
                            .vmName(vm.vmName())
                            .uuid(vm.uuid())
                            .status(result.getStatus())
                            .ipAddress(vm.ipAddress())
                            .plan(plan)
                            .createdAt(vm.createdAt())
                            .expiresAt(vm.expiresAt())
                            .resources(VmStatusResponse.ResourceStats.builder()
                                .cpu(VmStatusResponse.CpuStats.builder()
                                    .percent(result.getCpuPercent())
                                    .build())
                                .memory(VmStatusResponse.MemoryStats.builder()
                                    .totalMb(result.getMemTotalMb())
                                    .usedMb(result.getMemUsedMb())
                                    .availableMb(result.getMemAvailableMb())
                                    .build())
                                .disk(diskStats)
                                .build())
                            .build();
                    });
            }));
    }

    @Override
    public Mono<Void> stopBySystem(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> nodeService.getById(vm.getIdNode())
                .flatMap(node -> Mono.fromCallable(() -> vmManagerClient.stopVm(vm.getUuid(), toNodeInfo(node)))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then()
                )
            );
    }

    @Override
    public Mono<Void> deleteBySystem(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> nodeService.getById(vm.getIdNode())
                .flatMap(node -> Mono.fromCallable(() -> vmManagerClient.deleteVm(vm.getUuid(), toNodeInfo(node)))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then(vmRepository.deleteById(idVm))
                )
            );
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

    @Override
    public Mono<VncConsoleResponse> getConsole(Long idVm, Long idUser) {
        return getActiveVm(idVm, idUser)
            .flatMap(vm -> nodeService.getById(vm.idNode())
                .flatMap(node -> Mono.fromCallable(() -> vmManagerClient.getVmConsole(vm.uuid(), toNodeInfo(node)))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(console -> {
                        String token = UUID.randomUUID().toString();
                        String value = "localhost:" + console.getPort();

                        return redis.opsForValue()
                            .set(token, value, Duration.ofSeconds(60))
                            .thenReturn(new VncConsoleResponse(token, node.ipAddress()));
                    }))
            );
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

    private Mono<Vm> callGrpcCreate(Vm vm, PlanResponse plan, OsImageResponse osImage,
                                    VmManager.NodeInfo node, String password, String sshKey) {
        return ipPoolService.getByIdVM(vm.getIdVM())
            .flatMap(ipPool -> Mono.fromCallable(() ->
                vmManagerClient.createVm(vm.getVmName(), plan.ramMb(), plan.vcpus(),
                    plan.diskGb(), osImage.fileName(), ipPool.ipAddress(), password, sshKey, node)
                )
                .subscribeOn(Schedulers.boundedElastic())
            )
            .flatMap(grpcResponse -> {
                vm.setUuid(grpcResponse.getUuid());
                return vmRepository.save(vm);
            });
    }

    private Mono<Vm> callGrpcReinstall(Vm vm, OsImageResponse osImage, VmManager.NodeInfo node, String password, String sshKey) {
        return ipPoolService.getByIdVM(vm.getIdVM())
            .flatMap(ipPool -> Mono.fromCallable(() ->
                vmManagerClient.reinstallVm(vm.getUuid(), osImage.fileName(),
                    ipPool.ipAddress(), password, sshKey, node)
                )
                .subscribeOn(Schedulers.boundedElastic())
            )
            .flatMap(_ -> vmRepository.save(vm));
    }

    private VmResponse buildResponse(Vm vm, PlanResponse plan, String ipAddress) {
        return new VmResponse(vm.getIdVM(), vm.getVmName(), vm.getIdUser(), vm.getIdNode(), vm.getUuid(), ipAddress, vm.getIsActive(), vm.getIsBlocked(), vm.getCreatedAt(), vm.getExpiresAt(), plan);
    }

    private Mono<Void> executeVmAction(Long idVm, Long idUser, BiFunction<String, VmManager.NodeInfo, VmManager.VMResponse> action) {
        return getActiveVm(idVm, idUser).flatMap(vm ->
            nodeService.getById(vm.idNode()).flatMap(node ->
                Mono.fromCallable(() -> action.apply(vm.uuid(), toNodeInfo(node)))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then()
            )
        );
    }

    private VmManager.NodeInfo toNodeInfo(NodeResponse node) {
        return VmManager.NodeInfo.newBuilder()
            .setNodeId(node.idNode())
            .setIp(node.ipAddress())
            .setGrpcPort(node.grpcPort())
            .build();
    }
}