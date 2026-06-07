package dev.nelit.api.scheduler;

import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.vm.VmLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class VmLifecycleScheduler {

    private final VmRepository vmRepository;
    private final VmLifecycleService vmLifecycleService;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void blockExpired() {
        vmRepository.findAllByExpiresAtBeforeAndIsBlockedFalseAndIsActiveTrue(Instant.now().plus(1, ChronoUnit.MINUTES))
            .doOnNext(vm -> System.out.println("[scheduler] found expired vm: " + vm.getIdVM()))
            .flatMap(vm -> vmLifecycleService.block(vm.getIdVM())
                .doOnSuccess(v -> System.out.println("[scheduler] blocked vm: " + vm.getIdVM()))
                .doOnError(e -> System.out.println("[scheduler] error blocking vm " + vm.getIdVM() + ": " + e.getMessage()))
            )
            .subscribe();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteScheduled() {
        return;
    }
}
