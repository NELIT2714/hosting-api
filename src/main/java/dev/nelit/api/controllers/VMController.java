package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.vm.CreateVM;
import dev.nelit.api.dto.response.VMResponse;
import dev.nelit.api.services.VMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vms")
public class VMController {

    private final VMService vmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VMResponse> create(@RequestBody CreateVM vmDTO) {
        return vmService.create(vmDTO);
    }

}
