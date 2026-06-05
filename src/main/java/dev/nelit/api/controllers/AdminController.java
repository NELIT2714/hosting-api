package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermission;
import dev.nelit.api.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@Tag(name = "Admins (Admin)", description = "Admin account and permissions management — admin access only")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @Operation(
        summary = "Create an admin",
        description = "Creates a new admin account. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully",
                content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Admin with this email already exists",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AdminResponse> create(@RequestBody CreateAdmin createAdminDTO) {
        return adminService.create(createAdminDTO);
    }

    @Operation(
        summary = "Update admin permissions",
        description = "Replaces the full set of permissions for the specified admin. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Permissions updated successfully",
                content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Admin not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PatchMapping("/{admin_id}/permissions")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AdminResponse> update(@PathVariable("admin_id") Long adminId, @RequestBody Set<AdminPermission> permissions) {
        return adminService.update(adminId, permissions);
    }

    @Operation(
        summary = "Delete an admin",
        description = "Permanently removes an admin account by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Admin deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Admin not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/{admin_id}")
    public Mono<Void> delete(@PathVariable("admin_id") Long adminId) {
        return adminService.delete(adminId);
    }
}
