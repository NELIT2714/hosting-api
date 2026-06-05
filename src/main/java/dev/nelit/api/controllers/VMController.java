package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.vm.ActivateVM;
import dev.nelit.api.dto.response.VMResponse;
import dev.nelit.api.services.VMService;
import dev.nelit.api.services.orders.VpsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Virtual Machines", description = "VPS instance lifecycle management")
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

    @Operation(
        summary = "Get all VMs",
        description = "Returns all virtual machines associated with the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "List of VMs returned successfully",
                content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = VMResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @GetMapping
    public Flux<VMResponse> getAll() {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany(ctx -> {
                Long idUser = (Long) ctx.getAuthentication().getPrincipal();
                return vmService.getAllByUserId(idUser);
            });
    }

    @Operation(
        summary = "Activate a VM",
        description = "Activates the specified virtual machine by setting a root password and/or SSH key.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "VM activated successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "VM not found",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "VM already activated",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
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
