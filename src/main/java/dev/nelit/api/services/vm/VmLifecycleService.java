package dev.nelit.api.services.vm;

import reactor.core.publisher.Mono;

public interface VmLifecycleService {
    Mono<Void> block(Long idVm);
    Mono<Void> delete(Long idVm);
}
