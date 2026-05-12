package dev.nelit.api.dto.request.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record UpdateNode(
    @JsonProperty("id_node") Long idNode,
    @JsonProperty("node_name") String nodeName,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("grpc_port") Integer grpcPort,
    @JsonProperty("location") String location,
    @JsonProperty("is_active") Boolean isActive,
    @JsonProperty("created_at") Instant createdAt
) {}
