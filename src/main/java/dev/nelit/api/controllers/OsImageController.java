package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.osImage.CreateImage;
import dev.nelit.api.dto.response.OsImageResponse;
import dev.nelit.api.services.OsImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "OS Images (Admin)", description = "Operating system image management — admin access only")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/os-images")
public class OsImageController {

    private final OsImageService osImageService;

    @GetMapping
    public Flux<OsImageResponse> getAll() {
        return osImageService.getAll();
    }

    @Operation(
        summary = "Add an OS image",
        description = "Registers a new operating system image available for VPS deployment. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OS image created successfully",
                content = @Content(schema = @Schema(implementation = OsImageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    public Mono<OsImageResponse> create(@RequestBody CreateImage osImageDTO) {
        return osImageService.create(osImageDTO);
    }

    @Operation(
        summary = "Update an OS image",
        description = "Updates an operating system image by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OS image updated successfully",
                content = @Content(schema = @Schema(implementation = OsImageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "OS image not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PutMapping("/{os_image_id}")
    public Mono<OsImageResponse> update(@PathVariable("os_image_id") Long osImageId, @RequestBody CreateImage osImageDTO) {
        return osImageService.update(osImageId, osImageDTO);
    }

    @Operation(
        summary = "Delete an OS image",
        description = "Permanently removes an OS image by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OS image deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "OS image not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/{os_image_id}")
    public Mono<Void> delete(@PathVariable("os_image_id") Long osImageId) {
        return osImageService.delete(osImageId);
    }

}
