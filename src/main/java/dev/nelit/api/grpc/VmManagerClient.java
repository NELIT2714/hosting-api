package dev.nelit.api.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import vm_manager.VMManagerGrpc;
import vm_manager.VmManager;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VmManagerClient {

    private final VMManagerGrpc.VMManagerBlockingStub stub;

    public VmManager.VMResponse createVm(String vmName, int ramMb, int vcpus,
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
        if (sshKey != null && !sshKey.isBlank())  builder.setSshKey(sshKey);

        return stub.createVM(builder.build());
    }

    public VmManager.NodeInfo pickNode(List<VmManager.NodeInfo> nodes) {
        return stub.pickNode(
            VmManager.PickNodeRequest.newBuilder().addAllNodes(nodes).build()
        );
    }

    public VmManager.VMResponse startVm(String uuid, VmManager.NodeInfo node) {
        return stub.startVM(vmRequest(uuid, node));
    }

    public VmManager.VMResponse stopVm(String uuid, VmManager.NodeInfo node) {
        return stub.stopVM(vmRequest(uuid, node));
    }

    public VmManager.VMResponse getStatus(String uuid, VmManager.NodeInfo node) {
        return stub.getVM(vmRequest(uuid, node));
    }

    public VmManager.ConsoleResponse getVmConsole(String uuid, VmManager.NodeInfo node) {
        return stub.getVMConsole(vmRequest(uuid, node));
    }

    public VmManager.VMResponse deleteVm(String uuid, VmManager.NodeInfo node) {
        return stub.deleteVM(vmRequest(uuid, node));
    }

    public VmManager.VMResponse restartVm(String uuid, VmManager.NodeInfo node) {
        return stub.restartVM(vmRequest(uuid, node));
    }

    public VmManager.VMResponse reinstallVm(String uuid, String baseImage,
                                            String ipAddress, String password,
                                            String sshKey, VmManager.NodeInfo node) {
        VmManager.ReinstallVMRequest.Builder builder = VmManager.ReinstallVMRequest.newBuilder()
            .setUuid(uuid)
            .setBaseImage(baseImage)
            .setIpAddress(ipAddress)
            .setNode(node);

        if (password != null && !password.isBlank()) builder.setVmPassword(password);
        if (sshKey != null && !sshKey.isBlank()) builder.setSshKey(sshKey);

        return stub.reinstallVM(builder.build());
    }

    private VmManager.VMRequest vmRequest(String uuid, VmManager.NodeInfo node) {
        return VmManager.VMRequest.newBuilder()
            .setUuid(uuid)
            .setNode(node)
            .build();
    }
}