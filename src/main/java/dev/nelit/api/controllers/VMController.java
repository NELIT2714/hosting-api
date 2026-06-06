package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.vm.ActivateVM;
import dev.nelit.api.dto.response.VM.VmResponse;
import dev.nelit.api.dto.response.VM.VmStatusResponse;
import dev.nelit.api.services.vm.VmService;
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

import java.util.Objects;

@Tag(name = "Virtual Machines", description = "VPS instance lifecycle management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vms")
public class VMController {

    private final VmService vmService;
    private final VpsOrderService vpsOrderService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<VmResponse> create(@RequestBody CreateVM vmDTO) {
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
                    schema = @Schema(implementation = VmResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @GetMapping
    public Flux<VmResponse> getAll() {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return vmService.getAllByUserId(idUser);
            });
    }

    @Operation(
        summary = "Activate a Vm",
        description = "Activates the specified virtual machine by setting a root password and/or SSH key.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Vm activated successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Vm not found",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Vm already activated",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/{vm_id}/activate")
    public Mono<Void> activate(@RequestBody ActivateVM request, @PathVariable("vm_id") Long idVm) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return vmService.activate(idVm, idUser, request.password(), request.sshKey());
            });
    }

    @Operation(
        summary = "Get Vm status",
        description = "Returns current status and resource usage of the specified virtual machine",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Vm status returned successfully",
                content = @Content(schema = @Schema(implementation = VmStatusResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Vm not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @GetMapping("/{vm_id}/status")
    public Mono<VmStatusResponse> get(@PathVariable("vm_id") Long idVm) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return vmService.getStatus(idVm, idUser);
            });
    }

    @Operation(
        summary = "Start a Vm",
        description = "Starts the specified virtual machine",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Vm started successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Vm not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/{vm_id}/start")
    public Mono<Void> start(@PathVariable("vm_id") Long idVm) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return vmService.start(idVm, idUser);
            });
    }

    @Operation(
        summary = "Stop a Vm",
        description = "Stops the specified virtual machine",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Vm stopped successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Vm not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/{vm_id}/stop")
    public Mono<Void> stop(@PathVariable("vm_id") Long idVm) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return vmService.stop(idVm, idUser);
            });
    }
}
