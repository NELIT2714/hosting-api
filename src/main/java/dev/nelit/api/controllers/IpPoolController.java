package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.ipPool.CreateIP;
import dev.nelit.api.dto.response.IpPoolResponse;
import dev.nelit.api.services.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ip-pool")
public class IpPoolController {

    private final IpPoolService ipPoolService;

    @PostMapping
    public Mono<IpPoolResponse> create(@RequestBody CreateIP createIP) {
        return ipPoolService.create(createIP);
    }

    @DeleteMapping("/id/{id_ip}")
    public Mono<Void> delete(@PathVariable("id_ip") Long idIp) {
        return ipPoolService.delete(idIp);
    }

    @DeleteMapping("/ip/{ip_address}")
    public Mono<Void> delete(@PathVariable("ip_address") String ipAddress) {
        return ipPoolService.delete(ipAddress);
    }

}
