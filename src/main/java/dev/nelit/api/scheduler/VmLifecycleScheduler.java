package dev.nelit.api.scheduler;

import dev.nelit.api.repository.vm.VmLifecycleRepository;
import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.vm.VmLifecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class VmLifecycleScheduler {

    private final VmRepository vmRepository;
    private final VmLifecycleService vmLifecycleService;
    private final VmLifecycleRepository vmLifecycleRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void runDailyLifecycle() {
        log.info("[scheduler] daily lifecycle started");
        blockExpired()
            .then(deleteScheduled())
            .then(deleteInactive())
            .doOnTerminate(() -> log.info("[scheduler] daily lifecycle completed"))
            .subscribe(null, e -> log.error("[scheduler] unhandled error: {}", e.getMessage()));
    }

    private Mono<Void> blockExpired() {
        return vmRepository.findAllByExpiresAtBeforeAndIsBlockedFalseAndIsActiveTrue(Instant.now().plus(1, ChronoUnit.MINUTES))
            .flatMap(vm -> vmLifecycleService.block(vm.getIdVM())
                .doOnSuccess(v -> log.info("[scheduler] blocked vm: {}", vm.getIdVM()))
                .onErrorResume(e -> {
                    log.error("[scheduler] failed to block vm {}: {}", vm.getIdVM(), e.getMessage());
                    return Mono.empty();
                })
            )
            .then();
    }

    private Mono<Void> deleteScheduled() {
        return vmLifecycleRepository.findAllByDeleteAtBefore(Instant.now().plus(1, ChronoUnit.MINUTES))
            .flatMap(lifecycle -> vmLifecycleService.delete(lifecycle.getIdVm())
                .doOnSuccess(v -> log.info("[scheduler] deleted vm: {}", lifecycle.getIdVm()))
                .onErrorResume(e -> {
                    log.error("[scheduler] failed to delete vm {}: {}", lifecycle.getIdVm(), e.getMessage());
                    return Mono.empty();
                })
            )
            .then();
    }

    private Mono<Void> deleteInactive() {
        return vmRepository.findAllByExpiresAtBeforeAndIsActiveFalse(Instant.now().plus(1, ChronoUnit.MINUTES))
            .flatMap(vm -> vmRepository.deleteById(vm.getIdVM())
                .doOnSuccess(v -> log.info("[scheduler] deleted inactive vm: {}", vm.getIdVM()))
                .onErrorResume(e -> {
                    log.error("[scheduler] failed to delete inactive vm {}: {}", vm.getIdVM(), e.getMessage());
                    return Mono.empty();
                })
            )
            .then();
    }
}
