package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.vm.ActivateVM;
import dev.nelit.api.dto.response.VMResponse;
import dev.nelit.api.services.VMService;
import dev.nelit.api.services.orders.VpsOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vms")
public class VMController {

    private final VMService vmService;
    private final VpsOrderService vpsOrderService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<VMResponse> create(@RequestBody CreateVM vmDTO) {
//        return ReactiveSecurityContextHolder.getContext()
//            .flatMap(ctx -> {
//                Long idUser = (Long) ctx.getAuthentication().getPrincipal();
//                return vmService.setup(vmDTO, idUser);
//            });
//    }

    @GetMapping
    public Flux<VMResponse> getAll() {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany(ctx -> {
                Long idUser = (Long) ctx.getAuthentication().getPrincipal();
                return vmService.getAllByUserId(idUser);
            });
    }

    @PostMapping("/{vm_id}/activate")
    public Mono<Void> activate(@RequestBody ActivateVM request, @PathVariable("vm_id") Long idVm) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) ctx.getAuthentication().getPrincipal();
                return vpsOrderService.getByIdVm(idVm)
                    .flatMap(order -> vmService.activate(order.getIdVm(), order, request.password(), request.sshKey()));
            });
    }
}
