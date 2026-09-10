package dev.nelit.api.grpc;

import dev.nelit.api.grpc.support.GrpcReactorBridge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import vm_manager.VMManagerGrpc;
import vm_manager.VmManager;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VmLifecycleGrpcClient {

    private static final Duration CALL_DEADLINE = Duration.ofSeconds(10);

    private final VMManagerGrpc.VMManagerStub stub;

    public Mono<VmManager.VMResponse> createVm(String vmName, int ramMb, int vcpus,
                                               int diskGb, String baseImage,
                                               String ipAddress, String password,
                                               String sshKey, VmManager.NodeInfo node) {
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

        return GrpcReactorBridge.unaryCall(withDeadline()::createVM, builder.build(), "CreateVM");
    }

    public Mono<VmManager.VMResponse> startVm(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::startVM, vmRequest(uuid, node), "StartVM");
    }

    public Mono<VmManager.VMResponse> stopVm(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::stopVM, vmRequest(uuid, node), "StopVM");
    }

    public Mono<VmManager.VMResponse> getStatus(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::getVM, vmRequest(uuid, node), "GetStatus");
    }

    public Mono<VmManager.ConsoleResponse> getVmConsole(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::getVMConsole, vmRequest(uuid, node), "GetVmConsole");
    }

    public Mono<VmManager.VMResponse> deleteVm(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::deleteVM, vmRequest(uuid, node), "DeleteVM");
    }

    public Mono<VmManager.VMResponse> restartVm(String uuid, VmManager.NodeInfo node) {
        return GrpcReactorBridge.unaryCall(withDeadline()::restartVM, vmRequest(uuid, node), "RestartVM");
    }

    public Mono<VmManager.VMResponse> reinstallVm(String uuid, String baseImage,
                                                  String ipAddress, String password,
                                                  String sshKey, VmManager.NodeInfo node) {
        VmManager.ReinstallVMRequest.Builder builder = VmManager.ReinstallVMRequest.newBuilder()
            .setUuid(uuid)
            .setBaseImage(baseImage)
            .setIpAddress(ipAddress)
            .setNode(node);

        if (password != null && !password.isBlank()) builder.setVmPassword(password);
        if (sshKey != null && !sshKey.isBlank()) builder.setSshKey(sshKey);

        return GrpcReactorBridge.unaryCall(withDeadline()::reinstallVM, builder.build(), "ReinstallVM");
    }

    private VMManagerGrpc.VMManagerStub withDeadline() {
        return stub.withDeadlineAfter(CALL_DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }

    private VmManager.VMRequest vmRequest(String uuid, VmManager.NodeInfo node) {
        return VmManager.VMRequest.newBuilder()
            .setUuid(uuid)
            .setNode(node)
            .build();
    }
}
