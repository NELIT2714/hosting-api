package dev.nelit.api.services;

import dev.nelit.api.dto.request.osImage.CreateImage;
import dev.nelit.api.dto.response.OsImageResponse;
import reactor.core.publisher.Mono;

public interface OsImageService {
    Mono<OsImageResponse> getById(Long osImageId);
    Mono<OsImageResponse> create(CreateImage createImageDTO);
    Mono<Void> delete(Long osImageId);
}
