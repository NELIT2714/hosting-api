package dev.nelit.api.dto.request.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateNode(
   @JsonProperty("node_name") String nodeName,
   @JsonProperty("ip_address") String ipAddress,
   @JsonProperty("grpc_port") String grpcPort,
   @JsonProperty("location") String location,
   @JsonProperty("is_active") boolean isActive
) {}
