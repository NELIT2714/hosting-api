package dev.nelit.api.dto.request.ipPool;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateIP(
   @JsonProperty("id_node") Long idNode,
   @JsonProperty("ip_address") String ipAddress
) {}
