package dev.nelit.api.services.impl;

import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import dev.nelit.api.services.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IpPoolServiceImpl implements IpPoolService {

    @Override
    public Mono<IpPoolResponse> create(CreateIP createIP) {
        return null;
    }

    @Override
    public Mono<IpPoolResponse> assign(Long ipId, Long vmId) {
        return null;
    }

    @Override
    public Mono<Void> delete(Long ipId) {
        return null;
    }

    @Override
    public Mono<Void> delete(String ipAddress) {
        return null;
    }
}
