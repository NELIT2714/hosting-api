package dev.nelit.api.scheduler;

import dev.nelit.api.services.promo.PromoCodeUseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromoCodeCleanupScheduler {

    private final PromoCodeUseService promoCodeUseService;


    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void releaseExpiredReservations() {
        promoCodeUseService.releaseExpired()
            .subscribe(null, error ->
                log.error("Failed to release expired promo code reservations", error));
    }
}
