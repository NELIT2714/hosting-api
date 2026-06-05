package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import dev.nelit.api.services.IpPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "IP Pool (Admin)", description = "IP address pool management — admin access only")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ip-pool")
public class IpPoolController {

    private final IpPoolService ipPoolService;

    @Operation(
        summary = "Add an IP address",
        description = "Registers a new IP address in the pool for VPS allocation. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "IP address added successfully",
                content = @Content(schema = @Schema(implementation = IpPoolResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "IP address already exists",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    public Mono<IpPoolResponse> create(@RequestBody CreateIP createIP) {
        return ipPoolService.create(createIP);
    }

    @Operation(
        summary = "Delete an IP by ID",
        description = "Permanently removes an IP address from the pool by its internal ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "IP address deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "IP address not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/id/{id_ip}")
    public Mono<Void> delete(@PathVariable("id_ip") Long idIp) {
        return ipPoolService.delete(idIp);
    }

    @Operation(
        summary = "Delete an IP by address",
        description = "Permanently removes an IP address from the pool by its address value. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "IP address deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "IP address not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/ip/{ip_address}")
    public Mono<Void> delete(@PathVariable("ip_address") String ipAddress) {
        return ipPoolService.delete(ipAddress);
    }

}
