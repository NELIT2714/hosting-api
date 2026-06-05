package dev.nelit.api.services;

import dev.nelit.api.dto.request.osImage.CreateImage;
import dev.nelit.api.dto.response.OsImageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OsImageService {
    Flux<OsImageResponse> getAll();
    Mono<OsImageResponse> getById(Long osImageId);
    Mono<OsImageResponse> update(Long osImageId, CreateImage updateImageDTO);
    Mono<OsImageResponse> create(CreateImage createImageDTO);
    Mono<Void> delete(Long osImageId);
}
