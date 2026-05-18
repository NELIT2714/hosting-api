package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpPoolResponse(
    @JsonProperty("id_ip") Long idIp,
    @JsonProperty("id_node") Long idNode,
    @JsonProperty("id_vm") Long idVm,
    @JsonProperty("ip_address") String ipAddress
) {}
