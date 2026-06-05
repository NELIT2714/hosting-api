package dev.nelit.api.dto.request.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePlan(
    @JsonProperty("plan_name") @NotBlank String planName,
    @JsonProperty("ram_mb") @NotNull int ramMb,
    @JsonProperty("vcpus") @NotNull int vcpus,
    @JsonProperty("disk_gb") @NotNull int diskGb,
    @JsonProperty("price_per_month") @NotNull BigDecimal pricePerMonth,
    @JsonProperty("is_active") @NotNull Boolean isActive
) {}
