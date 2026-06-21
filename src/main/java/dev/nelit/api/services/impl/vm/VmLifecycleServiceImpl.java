package dev.nelit.api.services.impl.vm;

import dev.nelit.api.domain.entity.vm.VmLifecycle;
import dev.nelit.api.domain.exception.vm.VmNotFoundException;
import dev.nelit.api.repository.vm.VmLifecycleRepository;
import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.vm.VmLifecycleService;
import dev.nelit.api.services.vm.VmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VmLifecycleServiceImpl implements VmLifecycleService {

    private final VmRepository vmRepository;
    private final VmLifecycleRepository vmLifecycleRepository;
    private final VmService vmService;

    @Override
    public Mono<Void> block(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> {
                vm.setIsBlocked(true);
                Instant now = Instant.now();
                VmLifecycle lifecycle = VmLifecycle.builder()
                    .idVm(idVm)
                    .blockedAt(now)
                    .deleteAt(now.plus(7, ChronoUnit.DAYS))
                    .build();
                return vmRepository.save(vm)
                    .doOnSuccess(v -> log.info("[block] vm {} saved", idVm))
                    .flatMap(savedVm -> vmLifecycleRepository.save(lifecycle))
                    .doOnSuccess(v -> log.info("[block] lifecycle for vm {} saved", idVm))
                    .doOnError(e -> log.error("[block] lifecycle save error: {}", e.getMessage()))
                    .flatMap(savedLifecycle -> vmService.stopBySystem(idVm))
                    .doOnTerminate(() -> log.info("[block] vm {} stop completed", idVm))
                    .doOnError(e -> log.error("[block] stop error: {}", e.getMessage()));
            });
    }

    @Override
    public Mono<Void> unblock(Long idVm) {
        return vmRepository.findById(idVm)
            .switchIfEmpty(Mono.error(new VmNotFoundException()))
            .flatMap(vm -> {
                vm.setIsBlocked(false);
                return vmRepository.save(vm).then(vmLifecycleRepository.deleteByIdVm(idVm));
            });
    }

    @Override
    public Mono<Void> unblockIfBlocked(Long idVm) {
        return vmRepository.findById(idVm)
            .flatMap(vm -> {
                if (Boolean.FALSE.equals(vm.getIsBlocked())) return Mono.empty();
                return unblock(idVm);
            });
    }

    @Override
    public Mono<Void> delete(Long idVm) {
        return vmService.deleteBySystem(idVm);
    }
}
