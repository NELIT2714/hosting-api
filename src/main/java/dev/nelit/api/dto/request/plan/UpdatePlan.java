package dev.nelit.api.dto.request.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePlan(
    @JsonProperty("plan_name") @NotBlank String planName,
    @JsonProperty("ram_mb") @NotNull Integer ramMb,
    @JsonProperty("vcpus") @NotNull Integer vcpus,
    @JsonProperty("disk_gb") @NotNull Integer diskGb,
    @JsonProperty("price_per_month") @NotNull BigDecimal pricePerMonth,
    @JsonProperty("is_active") @NotNull Boolean isActive
) {}