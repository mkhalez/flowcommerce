package com.coworking.space.orderservice.event.payment;

import com.coworking.space.orderservice.domain.entities.PaymentEventEntity;
import com.coworking.space.orderservice.domain.enums.Status;
import com.coworking.space.orderservice.dto.broker.avro.PaymentOrderEvent;
import com.coworking.space.orderservice.dto.broker.avro.RegistrationStatus;
import com.coworking.space.orderservice.repositories.OrderRepository;
import com.coworking.space.orderservice.repositories.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventWriter {
    private final PaymentEventRepository paymentEventRepo;
    private final OrderRepository orderRepo;

    private static final String EVENT_ALREADY_PROCESSED = "payment event already processed with id: ";

    @Transactional
    public void changeOrderStatus(PaymentOrderEvent paymentOrderEvent, String eventId) {
        if(paymentEventRepo.existsById(eventId)) {
            log.atInfo().log(EVENT_ALREADY_PROCESSED + eventId);
            return;
        }

        var orderEntity = orderRepo.findById(paymentOrderEvent.getOrderId()).orElse(null);
        if(orderEntity == null) {
            return;
        }

        Status newStatus = paymentOrderEvent.getStatus() == RegistrationStatus.SUCCESS ? Status.READY_FOR_DELIVERY : Status.CANCELED;
        orderEntity.setStatus(newStatus);

        PaymentEventEntity newPaymentEventEntity = PaymentEventEntity.builder()
                .id(eventId)
                .build();
        paymentEventRepo.save(newPaymentEventEntity);
        orderRepo.save(orderEntity);
    }
}
