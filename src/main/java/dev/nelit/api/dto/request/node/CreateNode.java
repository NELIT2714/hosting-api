package dev.nelit.api.dto.request.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNode(
   @JsonProperty("node_name") @NotBlank String nodeName,
   @JsonProperty("ip_address") @NotBlank String ipAddress,
   @JsonProperty("grpc_port") @NotNull int grpcPort,
   @JsonProperty("location") @NotBlank String location,
   @JsonProperty("is_active") @NotNull boolean isActive
) {}
