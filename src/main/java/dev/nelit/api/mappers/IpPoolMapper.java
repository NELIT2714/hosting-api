package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.IpPool;
import dev.nelit.api.dto.response.IpPoolResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IpPoolMapper {
    IpPoolResponse toResponse(IpPool ipPool);
}
