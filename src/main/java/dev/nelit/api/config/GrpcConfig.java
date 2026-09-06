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
import java.io.File;

@Configuration
public class GrpcConfig {

    @Bean
    public VMManagerGrpc.VMManagerBlockingStub vmManagerStub(
        @Value("${grpc.vm-manager.host}") String host,
        @Value("${grpc.vm-manager.port}") int port,
        @Value("${pki.api-cert}") String certPath,
        @Value("${pki.api-key}") String keyPath,
        @Value("${pki.ca-cert}") String caCertPath
    ) throws Exception {

        SslContext sslContext = GrpcSslContexts.forClient()
            .trustManager(new File(caCertPath))
            .keyManager(new File(certPath), new File(keyPath))
            .build();

        ManagedChannel channel = NettyChannelBuilder
            .forAddress(host, port)
            .sslContext(sslContext)
            .build();

        return VMManagerGrpc.newBlockingStub(channel);
    }
}
