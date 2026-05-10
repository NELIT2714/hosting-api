package dev.nelit.api.dto.request.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record UpdatePlan(
    @JsonProperty("plan_name") String planName,
    @JsonProperty("ram_mb") Integer ramMb,
    @JsonProperty("vcpus") Integer vcpus,
    @JsonProperty("disk_gb") Integer diskGb,
    @JsonProperty("price_per_month") BigDecimal pricePerMonth,
    @JsonProperty("max_count") Integer maxCount,
    @JsonProperty("max_uplink_mbps") Integer maxUplinkMbps,
    @JsonProperty("is_active") Boolean isActive
) {}