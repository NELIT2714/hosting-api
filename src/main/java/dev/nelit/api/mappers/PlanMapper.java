package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.Plan;
import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.dto.response.plan.PlanResponse;
import dev.nelit.api.dto.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    PlanResponse toResponse(Plan plan);
}