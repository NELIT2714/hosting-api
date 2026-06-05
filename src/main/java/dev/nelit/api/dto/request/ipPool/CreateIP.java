package dev.nelit.api.dto.request.ipPool;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CreateIP(
   @JsonProperty("id_node") @NotNull Long idNode,
   @JsonProperty("ip_address") @NotNull String ipAddress
) {}
