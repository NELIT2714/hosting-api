package dev.nelit.api.config;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import vm_manager.VMManagerGrpc;

import java.io.ByteArrayInputStream;

@Configuration
public class GrpcConfig {

    @Bean
    public VMManagerGrpc.VMManagerBlockingStub vmManagerStub(@Value("${grpc.vm-manager.host}") String host, @Value("${grpc.vm-manager.port}") int port) throws Exception {
        byte[] caCert = new ClassPathResource("certs/ca.crt").getInputStream().readAllBytes();
        byte[] clientCert = new ClassPathResource("certs/client.crt").getInputStream().readAllBytes();
        byte[] clientKey = new ClassPathResource("certs/client.key").getInputStream().readAllBytes();

        SslContext sslContext = GrpcSslContexts.forClient()
            .trustManager(new ByteArrayInputStream(caCert))
            .keyManager(new ByteArrayInputStream(clientCert), new ByteArrayInputStream(clientKey))
            .build();

        ManagedChannel channel = NettyChannelBuilder
            .forAddress(host, port)
            .sslContext(sslContext)
            .build();

        return VMManagerGrpc.newBlockingStub(channel);
    }
}
