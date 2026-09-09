package com.coworking.space.paymentservice.mappers;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.dto.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(PaymentEntity entity);
}
