package dev.nelit.api.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import vm_manager.VMManagerGrpc;
import vm_manager.VmManager;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.nelit.api.grpc.support.GrpcReactorBridge.unaryCall;

@Component
@RequiredArgsConstructor
public class NodeSelectionGrpcClient {

    private static final Duration CALL_DEADLINE = Duration.ofSeconds(10);

    private final VMManagerGrpc.VMManagerStub stub;

    public Mono<VmManager.NodeInfo> pickNode(List<VmManager.NodeInfo> nodes) {
        return unaryCall(withDeadline()::pickNode,
            VmManager.PickNodeRequest.newBuilder().addAllNodes(nodes).build());
    }

    private VMManagerGrpc.VMManagerStub withDeadline() {
        return stub.withDeadlineAfter(CALL_DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }
}
