package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.VMResponse;
import dev.nelit.api.services.VMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vms")
public class VMController {

    private final VMService vmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VMResponse> create(@RequestBody CreateVM vmDTO) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Authentication authentication = ctx.getAuthentication();
                if (authentication == null) return Mono.error(new AccessDeniedException("Not authenticated"));
                Long idUser = (Long) authentication.getPrincipal();
                return vmService.setup(vmDTO, idUser);
            });
    }

}
