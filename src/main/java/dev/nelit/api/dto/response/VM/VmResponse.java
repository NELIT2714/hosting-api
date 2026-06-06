package dev.nelit.api.dto.response.VM;

import dev.nelit.api.dto.response.PlanResponse;

import java.time.Instant;

public record VmResponse(
    Long idVm,
    String vmName,
    Long idUser,
    Long idNode,
    String uuid,
    String ipAddress,
    Boolean isActive,
    Instant createdAt,
    Instant expiresAt,
    PlanResponse plan
) {}
