package dev.nelit.api.dto.request.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CreatePlan(
    @JsonProperty("plan_name") String planName,
    @JsonProperty("ram_mb") int ramMb,
    @JsonProperty("vcpus") int vcpus,
    @JsonProperty("disk_gb") int diskGb,
    @JsonProperty("price_per_month") BigDecimal pricePerMonth,
    @JsonProperty("max_count") int maxCount,
    @JsonProperty("max_uplink_mbps") int maxUplinkMbps,
    @JsonProperty("is_active") Boolean isActive
) {}
