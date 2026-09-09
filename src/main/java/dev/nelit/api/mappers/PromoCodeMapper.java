package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.dto.request.promo.UpdatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromoCodeMapper {
    PromoCodeResponse toResponse(PromoCode promoCode);
    void update(UpdatePromoCode dto, @MappingTarget PromoCode promoCode);
}
