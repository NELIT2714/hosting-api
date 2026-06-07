package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.dto.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
}
