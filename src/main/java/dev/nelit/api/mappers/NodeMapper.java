package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.Node;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.NodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeMapper {
    NodeResponse toResponse(Node node);
    void update(UpdateNode dto, @MappingTarget Node node);
}
