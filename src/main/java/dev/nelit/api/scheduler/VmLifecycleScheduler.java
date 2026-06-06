package dev.nelit.api.scheduler;

import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.vm.VmLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VmLifecycleScheduler {

    private final VmRepository vmRepository;
    private final VmLifecycleService vmLifecycleService;

    @Scheduled(cron = "0 0 * * * *")
    public void blockExpired() {
        vmRepository.findAllByExpiresAtBeforeAndIsBlockedFalse(Instant.now())
            .flatMap(vm -> vmLifecycleService.block(vm.getIdVM()))
            .subscribe();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteScheduled() {
        return;
    }
}
