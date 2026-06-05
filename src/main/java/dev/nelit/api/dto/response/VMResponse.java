package dev.nelit.api.dto.response;

import java.time.Instant;

public record VMResponse(
    Long idVm,
    String vmName,
    String uuid,
    String ipAddress,
    Boolean isActive,
    Instant createdAt,
    Instant expiresAt,
    PlanResponse plan
) {}
