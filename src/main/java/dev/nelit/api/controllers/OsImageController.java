package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.osImage.CreateImage;
import dev.nelit.api.dto.response.OsImageResponse;
import dev.nelit.api.services.OsImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/os-images")
public class OsImageController {

    private final OsImageService osImageService;

    @PostMapping
    public Mono<OsImageResponse> create(@RequestBody CreateImage osImageDTO) {
        return osImageService.create(osImageDTO);
    }

    @DeleteMapping("/{os_image_id}")
    public Mono<Void> delete(@PathVariable("os_image_id") Long osImageId) {
        return osImageService.delete(osImageId);
    }

}
