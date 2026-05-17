package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.OsImage;
import dev.nelit.api.domain.exception.osImage.OsImageAlreadyExists;
import dev.nelit.api.domain.exception.osImage.OsImageNotFound;
import dev.nelit.api.dto.request.osImage.CreateImage;
import dev.nelit.api.dto.response.OsImageResponse;
import dev.nelit.api.mappers.OsImageMapper;
import dev.nelit.api.repository.OsImageRepository;
import dev.nelit.api.services.OsImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OsImageServiceImpl implements OsImageService {

    private final OsImageRepository osImageRepository;
    private final OsImageMapper osImageMapper;

    @Override
    public Mono<OsImageResponse> getById(Long osImageId) {
        return osImageRepository.findById(osImageId)
            .switchIfEmpty(Mono.error(new OsImageNotFound()))
            .map(osImageMapper::toResponse);
    }

    @Override
    public Mono<OsImageResponse> create(CreateImage createImageDTO) {
        OsImage osImage = OsImage.builder()
            .imageName(createImageDTO.imageName())
            .fileName(createImageDTO.fileName())
            .build();

        return osImageRepository.save(osImage)
            .onErrorResume(e -> Mono.error(new OsImageAlreadyExists()))
            .map(osImageMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long osImageId) {
        return osImageRepository.deleteById(osImageId)
            .onErrorResume(e -> Mono.error(new OsImageNotFound()));
    }

}
