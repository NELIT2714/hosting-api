package dev.nelit.api.services.impl.vm;

import dev.nelit.api.domain.entity.VmLifecycle;
import dev.nelit.api.repository.vm.VmLifecycleRepository;
import dev.nelit.api.repository.vm.VmRepository;
import dev.nelit.api.services.vm.VmLifecycleService;
import dev.nelit.api.services.vm.VmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
                    .then(vmLifecycleRepository.save(lifecycle))
                    .then(vmService.stopBySystem(idVm));
            });
    }

    @Override
    public Mono<Void> delete(Long idVm) {
        return vmService.deleteBySystem(idVm).then(vmLifecycleRepository.deleteById(idVm));
    }
}
