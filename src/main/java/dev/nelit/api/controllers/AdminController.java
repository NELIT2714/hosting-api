package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermissions;
import dev.nelit.api.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AdminResponse> create(@RequestBody CreateAdmin createAdminDTO) {
        return adminService.create(createAdminDTO);
    }

    @PatchMapping("/{admin_id}/permissions")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AdminResponse> update(@PathVariable("admin_id") Long adminId, @RequestBody Set<AdminPermissions> permissions) {
        return adminService.update(adminId, permissions);
    }

    @DeleteMapping("/{admin_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> delete(@PathVariable("admin_id") Long adminId) {
        return adminService.delete(adminId);
    }
}
