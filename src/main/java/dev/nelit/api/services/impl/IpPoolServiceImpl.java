package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.IpPool;
import dev.nelit.api.domain.exception.ipPool.IpAddressAlreadyAssignedException;
import dev.nelit.api.domain.exception.ipPool.IpAddressAlreadyExistsException;
import dev.nelit.api.domain.exception.ipPool.IpAddressNotFoundException;
import dev.nelit.api.domain.exception.ipPool.NoAvailableAddressesException;
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
    public Mono<Boolean> hasAvailable() {
        return ipPoolRepository.existsByIdVmIsNull();
    }

    @Override
    public Mono<IpPoolResponse> getFirstAvailable(Long idNode) {
        return ipPoolRepository.findFirstByIdVmIsNullAndIdNodeOrderByIdIpAsc(idNode)
            .switchIfEmpty(Mono.error(new NoAvailableAddressesException()))
            .map(ipPoolMapper::toResponse);
    }

    @Override
    public Mono<IpPoolResponse> getByIdVM(Long idVm) {
        return ipPoolRepository.findByIdVm(idVm)
            .switchIfEmpty(Mono.error(new IpAddressNotFoundException()))
            .map(ipPoolMapper::toResponse);
    }

    @Override
    public Mono<IpPoolResponse> create(CreateIP createIP) {
        IpPool ipPool = IpPool.builder()
            .idNode(createIP.idNode())
            .ipAddress(createIP.ipAddress())
            .build();

        return ipPoolRepository.save(ipPool)
            .onErrorMap(DuplicateKeyException.class, _ -> new IpAddressAlreadyExistsException())
            .map(ipPoolMapper::toResponse);
    }

    @Override
    public Mono<IpPoolResponse> assign(Long idIp, Long vmId) {
        return ipPoolRepository.findById(idIp)
            .switchIfEmpty(Mono.error(new IpAddressNotFoundException()))
            .flatMap(ipPool -> {
                if (ipPool.getIdVm() != null) {
                    return Mono.error(new IpAddressAlreadyAssignedException());
                }
                ipPool.setIdVm(vmId);
                return ipPoolRepository.save(ipPool);
            })
            .map(ipPoolMapper::toResponse);
    }

    @Override
    public Mono<Void> unassign(Long idIp) {
        return ipPoolRepository.findById(idIp)
            .switchIfEmpty(Mono.error(new IpAddressNotFoundException()))
            .flatMap(ipPool -> {
                ipPool.setIdVm(null);
                return ipPoolRepository.save(ipPool);
            })
            .then();
    }

    @Override
    public Mono<Void> delete(Long idIp) {
        return null;
    }

    @Override
    public Mono<Void> delete(String ipAddress) {
        return null;
    }
}
