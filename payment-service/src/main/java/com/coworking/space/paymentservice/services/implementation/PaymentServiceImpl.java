package com.coworking.space.paymentservice.services.implementation;

import com.coworking.space.paymentservice.clients.OrderServiceClient;
import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.exceptions.PaymentAlreadyProcessed;
import com.coworking.space.paymentservice.domain.exceptions.PaymentInPendingStatus;
import com.coworking.space.paymentservice.domain.exceptions.PaymentNotFoundException;
import com.coworking.space.paymentservice.domain.exceptions.UserServiceException;
import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import com.coworking.space.paymentservice.dto.response.OrderResponse;
import com.coworking.space.paymentservice.dto.response.PaymentResponse;
import com.coworking.space.paymentservice.dto.response.SumResult;
import com.coworking.space.paymentservice.infrastructure.properties.PaymentCreationProperties;
import com.coworking.space.paymentservice.mappers.PaymentMapper;
import com.coworking.space.paymentservice.repositories.PaymentRepository;
import com.coworking.space.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;
    private final OrderServiceClient orderServiceClient;
    private final PaymentCreationProperties paymentCreationProperties;
    private final BankServiceImpl bankService;

    private static final String PAYMENT_NOT_FOUND_EXCEPTION = "payment not found exception";
    private static final String PAYMENT_IN_PENDING_STATUS = "payment in pending status";
    private static final String PAYMENT_ALREADY_PROCESSED = "payment already processed";

    @Override
    public PaymentResponse createPayment(int orderId) {
        var entityOptional = paymentRepo.findByOrderId(orderId);

        PaymentEntity entity = null;
        if(entityOptional.isPresent()) {
             entity = entityOptional.get();
            if(entity.getStatus() == PaymentStatus.PENDING && entity.getPendingBoundary().isAfter(Instant.now())) {
                throw new PaymentInPendingStatus(PAYMENT_IN_PENDING_STATUS);
            }

            if(entity.getStatus() == PaymentStatus.SUCCESS) {
                throw new PaymentAlreadyProcessed(PAYMENT_ALREADY_PROCESSED);
            }
        }

        OrderResponse orderResponse;
        try {
            orderResponse = orderServiceClient.findById(orderId);
        } catch (Exception e) {
            log.atError().setCause(e).log();
            throw new UserServiceException(e);
        }

        ObjectId id = entity != null ? entity.getId() : null;
        PaymentEntity newPaymentEntity = new PaymentEntity(
                id,
                orderId,
                orderResponse.getUser().getId(),
                PaymentStatus.PENDING,
                Instant.now(),
                Instant.now().plus(paymentCreationProperties.getMinutesDelta(), ChronoUnit.MINUTES),
                orderResponse.getTotalPrice());

        paymentRepo.save(newPaymentEntity);

        if(bankService.isApproved()) {
            newPaymentEntity.setStatus(PaymentStatus.SUCCESS);
        } else {
            newPaymentEntity.setStatus(PaymentStatus.FAIL);
        }
        var savedEntity = paymentRepo.save(newPaymentEntity);

        return paymentMapper.toPaymentResponse(savedEntity);
    }

    @Override
    public PaymentResponse findByOrderId(int orderId) {
        var entity = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND_EXCEPTION));

        return paymentMapper.toPaymentResponse(entity);
    }

    @Override
    public List<PaymentResponse> findByUserId(int userId) {
        var entities = paymentRepo.findByUserId(userId);

        return entities.stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> findByStatus(PaymentStatus status) {
        var entities = paymentRepo.findByStatus(status);

        return entities.stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }

    @Override
    public SumResult sumOfPaymentsByUserId(int userId, OffsetDateTime from, OffsetDateTime to) {
        return paymentRepo.getSumByUser(from.toInstant(), to.toInstant(), userId);
    }

    @Override
    public SumResult sumOfPayments(OffsetDateTime from, OffsetDateTime to) {
        return paymentRepo.getTotalSum(from.toInstant(), to.toInstant());
    }
}
