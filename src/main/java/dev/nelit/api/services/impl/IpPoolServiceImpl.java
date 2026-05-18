package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.IpPool;
import dev.nelit.api.domain.exception.ipPool.IpAddressAlreadyExists;
import dev.nelit.api.domain.exception.user.EmailAlreadyExistsException;
import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import dev.nelit.api.mappers.IpPoolMapper;
import dev.nelit.api.repository.IpPoolRepository;
import dev.nelit.api.services.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IpPoolServiceImpl implements IpPoolService {

    private final IpPoolRepository ipPoolRepository;
    private final IpPoolMapper ipPoolMapper;

    @Override
    public Mono<IpPoolResponse> create(CreateIP createIP) {
        System.out.println(createIP.idNode());
        IpPool ipPool = IpPool.builder()
            .idNode(createIP.idNode())
            .ipAddress(createIP.ipAddress())
            .build();

        return ipPoolRepository.save(ipPool)
            .onErrorMap(DuplicateKeyException.class, _ -> new IpAddressAlreadyExists())
            .map(ipPoolMapper::toResponse);
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
