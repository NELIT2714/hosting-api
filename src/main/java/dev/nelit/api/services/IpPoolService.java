package dev.nelit.api.services;

import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import reactor.core.publisher.Mono;

public interface IpPoolService {
    Mono<IpPoolResponse> getFirstAvailable(Long idNode);
    Mono<IpPoolResponse> getByIdVM(Long idVm);
    Mono<IpPoolResponse> create(CreateIP createIP);
    Mono<IpPoolResponse> assign(Long idIp, Long vmId);
    Mono<Void> unassign(Long idIp);
    Mono<Void> delete(Long idIp);
    Mono<Void> delete(String ipAddress);
}
