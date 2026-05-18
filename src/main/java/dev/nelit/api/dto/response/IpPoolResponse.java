package dev.nelit.api.dto.response;

public record IpPoolResponse(
    Long idIp,
    Long idNode,
    Long idVm,
    String ipAddress
) {}
