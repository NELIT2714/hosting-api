package dev.nelit.api.dto.response;

public record IpPoolResponse(
    Long id,
    Long idNode,
    Long idVm,
    String ipAddress
) {}
