package dev.nelit.api.services;

import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import reactor.core.publisher.Mono;

public interface IpPoolService {
    Mono<IpPoolResponse> create(CreateIP createIP);
    Mono<IpPoolResponse> assign(Long ipId, Long vmId);
    Mono<Void> delete(Long ipId);
    Mono<Void> delete(String ipAddress);
}
