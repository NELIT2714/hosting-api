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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OsImageServiceImpl implements OsImageService {

    private final OsImageRepository osImageRepository;
    private final OsImageMapper osImageMapper;

    @Override
    public Flux<OsImageResponse> getAll() {
        return osImageRepository.findAll().map(osImageMapper::toResponse);
    }

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
            .onErrorMap(DuplicateKeyException.class, _ -> new OsImageAlreadyExists())
            .map(osImageMapper::toResponse);
    }

    @Override
    public Mono<OsImageResponse> update(Long osImageId, CreateImage updateImageDTO) {
        return osImageRepository.findById(osImageId)
            .switchIfEmpty(Mono.error(new OsImageNotFound()))
            .flatMap(existing -> {
                existing.setImageName(updateImageDTO.imageName());
                existing.setFileName(updateImageDTO.fileName());
                return osImageRepository.save(existing)
                    .onErrorMap(DuplicateKeyException.class, _ -> new OsImageAlreadyExists());
            })
            .map(osImageMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long osImageId) {
        return osImageRepository.deleteById(osImageId)
            .onErrorMap(DuplicateKeyException.class, _ -> new OsImageNotFound());
    }

}
