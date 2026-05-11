package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.Plan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.PlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlanMapper {
    PlanResponse toResponse(Plan plan);
    void update(UpdatePlan dto, @MappingTarget Plan plan);
}