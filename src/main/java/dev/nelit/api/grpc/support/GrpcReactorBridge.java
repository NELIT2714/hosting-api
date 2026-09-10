package dev.nelit.api.grpc.support;

import dev.nelit.api.domain.exception.grpc.VmManagerException;
import dev.nelit.api.domain.exception.grpc.VmManagerTimeoutException;
import dev.nelit.api.domain.exception.grpc.VmManagerUnavailableException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;

@Slf4j
public final class GrpcReactorBridge {

    private GrpcReactorBridge() {
    }

    public static <ReqT, RespT> Mono<RespT> unaryCall(BiConsumer<ReqT, StreamObserver<RespT>> call, ReqT request) {
        return unaryCall(call, request, "unary call");
    }

    public static <ReqT, RespT> Mono<RespT> unaryCall(BiConsumer<ReqT, StreamObserver<RespT>> call, ReqT request, String operation) {
        return Mono.<RespT>create(sink -> call.accept(request, new StreamObserver<RespT>() {
            private RespT result;

            @Override
            public void onNext(RespT value) {
                result = value;
            }

            @Override
            public void onError(Throwable t) {
                sink.error(t);
            }

            @Override
            public void onCompleted() {
                sink.success(result);
            }
        })).onErrorMap(StatusRuntimeException.class, e -> translate(e, operation, request));
    }

    private static Throwable translate(StatusRuntimeException e, String operation, Object request) {
        Status.Code code = e.getStatus().getCode();

        return switch (code) {
            case UNAVAILABLE -> {
                log.error("gRPC [{}] VM Manager unavailable. request={}, cause={}", operation, request, e.getStatus().getDescription(), e);
                yield new VmManagerUnavailableException();
            }
            case DEADLINE_EXCEEDED -> {
                log.warn("gRPC [{}] VM Manager timed out (10s). request={}", operation, request);
                yield new VmManagerTimeoutException();
            }
            default -> {
                log.error("gRPC [{}] VM Manager error: status={}, description={}, request={}", operation, code, e.getStatus().getDescription(), request, e);
                yield new VmManagerException(e.getStatus().getDescription());
            }
        };
    }
}
