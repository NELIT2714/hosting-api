package dev.nelit.api.services;

import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.VMResponse;
import reactor.core.publisher.Mono;

public interface VMService {
    Mono<VMResponse> create(CreateVM vmDTO, Long idUser);
}
