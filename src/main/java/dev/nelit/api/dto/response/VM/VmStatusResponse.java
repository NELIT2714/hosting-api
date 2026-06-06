package dev.nelit.api.dto.response.VM;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.dto.response.PlanResponse;
import lombok.Builder;

import java.time.Instant;

@Builder
public record VmStatusResponse(
    @JsonProperty("vm_name") String vmName,
    @JsonProperty("uuid") String uuid,
    @JsonProperty("status") String status,
    @JsonProperty("cpu_percent") double cpuPercent,
    @JsonProperty("mem_total_mb") double memTotalMb,
    @JsonProperty("mem_used_mb") double memUsedMb,
    @JsonProperty("mem_available_mb") double memAvailableMb,
    @JsonProperty("plan") PlanResponse plan,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("expires_at") Instant expiresAt
) {}
