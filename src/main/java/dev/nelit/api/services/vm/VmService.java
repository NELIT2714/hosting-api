package dev.nelit.api.services.vm;

import dev.nelit.api.domain.entity.vm.Vm;
import dev.nelit.api.dto.request.vm.ActivateVM;
import dev.nelit.api.dto.request.vm.ReinstallVM;
import dev.nelit.api.dto.response.VM.VmResponse;
import dev.nelit.api.dto.response.VM.VmStatusResponse;
import dev.nelit.api.dto.response.VM.VncConsoleResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VmService {
    Flux<VmResponse> getAllByUserId(Long idUser);
    Mono<VmResponse> getById(Long idVm);
    Mono<VmResponse> getActiveVm(Long idVm, Long idUser);

//    Mono<VmResponse> setup(CreateVM vmDTO, Long idUser);

    Mono<Vm> create(Long idUser, Long idPlan, Long idOsImage);
    Mono<Void> activate(Long idVm, Long idUser, ActivateVM request);
    Mono<Void> reinstall(Long idVm, Long idUser, ReinstallVM request);
    Mono<Void> renew(Long idVm, Integer days);

    Mono<Void> start(Long idVm, Long idUser);
    Mono<Void> stop(Long idVm, Long idUser);
    Mono<Void> restart(Long idVm, Long idUser);
    Mono<VmStatusResponse> getStatus(Long idVm, Long idUser);

    Mono<Void> stopBySystem(Long idVm);
    Mono<Void> deleteBySystem(Long idVm);

    // VNC
    Mono<VncConsoleResponse> getConsole(Long idVm, Long idUser);
}
