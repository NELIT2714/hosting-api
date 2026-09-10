package dev.nelit.api.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vm_manager.VMManagerGrpc;
import vm_manager.VmManager;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;


// Долгосрочно правильнее мигрировать на VMManagerGrpc.VMManagerStub (async) с
// reactor-grpc / grpc-java-стримингом, но это отдельная задача - этот фикс закрывает
// риск блокировки event loop без изменения остальной кодовой базы.
@Component
@RequiredArgsConstructor
public class VmManagerClient {

    private static final Duration CALL_DEADLINE = Duration.ofSeconds(10);

    private final VMManagerGrpc.VMManagerBlockingStub stub;

    public Mono<VmManager.VMResponse> createVm(String vmName, int ramMb, int vcpus,
                                               int diskGb, String baseImage,
                                               String ipAddress, String password,
                                               String sshKey, VmManager.NodeInfo node) {
        return callBlocking(() -> {
            VmManager.CreateVMRequest.Builder builder = VmManager.CreateVMRequest.newBuilder()
                .setVmName(vmName)
                .setRamMb(ramMb)
                .setVcpus(vcpus)
                .setDiskGb(diskGb)
                .setIpAddress(ipAddress)
                .setBaseImage(baseImage)
                .setNode(node);

            if (password != null && !password.isBlank()) builder.setVmPassword(password);
            if (sshKey != null && !sshKey.isBlank()) builder.setSshKey(sshKey);

            return withDeadline().createVM(builder.build());
        });
    }

    public Mono<VmManager.NodeInfo> pickNode(List<VmManager.NodeInfo> nodes) {
        return callBlocking(() -> withDeadline().pickNode(
            VmManager.PickNodeRequest.newBuilder().addAllNodes(nodes).build()
        ));
    }

    public Mono<VmManager.VMResponse> startVm(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().startVM(vmRequest(uuid, node)));
    }

    public Mono<VmManager.VMResponse> stopVm(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().stopVM(vmRequest(uuid, node)));
    }

    public Mono<VmManager.VMResponse> getStatus(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().getVM(vmRequest(uuid, node)));
    }

    public Mono<VmManager.ConsoleResponse> getVmConsole(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().getVMConsole(vmRequest(uuid, node)));
    }

    public Mono<VmManager.VMResponse> deleteVm(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().deleteVM(vmRequest(uuid, node)));
    }

    public Mono<VmManager.VMResponse> restartVm(String uuid, VmManager.NodeInfo node) {
        return callBlocking(() -> withDeadline().restartVM(vmRequest(uuid, node)));
    }

    public Mono<VmManager.VMResponse> reinstallVm(String uuid, String baseImage,
                                                  String ipAddress, String password,
                                                  String sshKey, VmManager.NodeInfo node) {
        return callBlocking(() -> {
            VmManager.ReinstallVMRequest.Builder builder = VmManager.ReinstallVMRequest.newBuilder()
                .setUuid(uuid)
                .setBaseImage(baseImage)
                .setIpAddress(ipAddress)
                .setNode(node);

            if (password != null && !password.isBlank()) builder.setVmPassword(password);
            if (sshKey != null && !sshKey.isBlank()) builder.setSshKey(sshKey);

            return withDeadline().reinstallVM(builder.build());
        });
    }

    private <T> Mono<T> callBlocking(java.util.function.Supplier<T> blockingCall) {
        return Mono.fromCallable(blockingCall::get)
            .subscribeOn(Schedulers.boundedElastic());
    }

    private VMManagerGrpc.VMManagerBlockingStub withDeadline() {
        return stub.withDeadlineAfter(CALL_DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }

    private VmManager.VMRequest vmRequest(String uuid, VmManager.NodeInfo node) {
        return VmManager.VMRequest.newBuilder()
            .setUuid(uuid)
            .setNode(node)
            .build();
    }
}