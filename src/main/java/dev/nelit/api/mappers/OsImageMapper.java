package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.OsImage;
import dev.nelit.api.dto.response.OsImageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OsImageMapper {
    OsImageResponse toResponse(OsImage osImage);
}
