package dev.nelit.api.services;

import dev.nelit.api.domain.entity.VM;
import dev.nelit.api.domain.entity.VpsOrder;
import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.VMResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VMService {
    Flux<VMResponse> getAllByUserId(Long idUser);
    Mono<VMResponse> setup(CreateVM vmDTO, Long idUser);
    Mono<VM> create(Long idUser, Long idPlan, Long idOsImage);
    Mono<Void> activate(Long idVm, VpsOrder vpsOrder, String password, String sshKey);
}
