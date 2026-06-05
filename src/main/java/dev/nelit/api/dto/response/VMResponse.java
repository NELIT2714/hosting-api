package dev.nelit.api.dto.response;

import java.time.Instant;

public record VMResponse(
    Long idVM,
    Long idUser,
    Long idPlan,
    String vmName,
    String uuid,
    String ipAddress,
    String status,
    Boolean isActive,
    Instant createdAt
) {}
