package dev.nelit.api.dto.request.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateNode(
    @JsonProperty("id_node") @NotNull Long idNode,
    @JsonProperty("node_name") @NotBlank String nodeName,
    @JsonProperty("ip_address") @NotBlank String ipAddress,
    @JsonProperty("grpc_port") @NotNull Integer grpcPort,
    @JsonProperty("location") @NotBlank String location,
    @JsonProperty("is_active") @NotNull Boolean isActive,
    @JsonProperty("created_at") @NotNull Instant createdAt
) {}
