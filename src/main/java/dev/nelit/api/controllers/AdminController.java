package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @DeleteMapping("/{admin_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> delete(@PathVariable("admin_id") Long adminId) {
        return adminService.delete(adminId);
    }
}
