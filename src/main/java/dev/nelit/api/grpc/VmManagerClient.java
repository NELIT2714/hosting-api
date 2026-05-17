package dev.nelit.api.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import vm_manager.VMManagerGrpc;
import vm_manager.VmManager;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class VmManagerClient {

    private final VMManagerGrpc.VMManagerBlockingStub stub;

    public VmManagerClient(
        @Value("${grpc.vm-manager.host}") String host,
        @Value("${grpc.vm-manager.port}") int port
    ) throws Exception {
        byte[] caCert = new ClassPathResource("certs/ca.crt").getInputStream().readAllBytes();
        byte[] clientCert = new ClassPathResource("certs/client.crt").getInputStream().readAllBytes();
        byte[] clientKey = new ClassPathResource("certs/client.key").getInputStream().readAllBytes();

        SslContext sslContext = GrpcSslContexts.forClient()
            .trustManager(new ByteArrayInputStream(caCert))
            .keyManager(
                new ByteArrayInputStream(clientCert),
                new ByteArrayInputStream(clientKey)
            )
            .build();

        ManagedChannel channel = NettyChannelBuilder
            .forAddress(host, port)
            .sslContext(sslContext)
            .build();

        this.stub = VMManagerGrpc.newBlockingStub(channel);
    }

    public VmManager.VMResponse createVm(String vmName, int ramMb, int vcpus,
                                         int diskGb, String baseImage,
                                         VmManager.NodeInfo node) {
        VmManager.CreateVMRequest request = VmManager.CreateVMRequest.newBuilder()
            .setVmName(vmName)
            .setRamMb(ramMb)
            .setVcpus(vcpus)
            .setDiskGb(diskGb)
            .setBaseImage(baseImage)
            .setNode(node)
            .build();

        return stub.createVM(request);
    }

    public VmManager.NodeInfo pickNode(List<VmManager.NodeInfo> nodes) {
        VmManager.PickNodeRequest request = VmManager.PickNodeRequest.newBuilder()
            .addAllNodes(nodes)
            .build();

        return stub.pickNode(request);
    }
}