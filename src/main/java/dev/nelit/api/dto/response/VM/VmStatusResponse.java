package dev.nelit.api.dto.response.VM;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.dto.response.PlanResponse;
import lombok.Builder;

import java.time.Instant;

@Builder
public record VmStatusResponse(
    @JsonProperty("id")         Long id,
    @JsonProperty("vm_name")    String vmName,
    @JsonProperty("uuid")       String uuid,
    @JsonProperty("status")     String status,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("plan")       PlanResponse plan,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("expires_at") Instant expiresAt,
    @JsonProperty("resources")  ResourceStats resources
) {
    @Builder
    public record ResourceStats(
        @JsonProperty("cpu")    CpuStats cpu,
        @JsonProperty("memory") MemoryStats memory,
        @JsonProperty("disk")   DiskStats disk
    ) {}

    @Builder
    public record CpuStats(
        @JsonProperty("percent") double percent
    ) {}

    @Builder
    public record MemoryStats(
        @JsonProperty("total_mb")     double totalMb,
        @JsonProperty("used_mb")      Double usedMb,
        @JsonProperty("available_mb") Double availableMb
    ) {}

    @Builder
    public record DiskStats(
        @JsonProperty("read_mb_s")  double readMbS,
        @JsonProperty("write_mb_s") double writeMbS,
        @JsonProperty("read_iops")  double readIops,
        @JsonProperty("write_iops") double writeIops
    ) {}
}