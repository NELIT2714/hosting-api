package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record NodeResponse(
    @JsonProperty("id_node") Long idNode,
    @JsonProperty("node_name") String nodeName,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("grpc_port") int grpcPort,
    @JsonProperty("location") String location,
    @JsonProperty("is_active") boolean isActive,
    @JsonProperty("created_at") Instant createdAt
) {}
