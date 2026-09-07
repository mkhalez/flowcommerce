package com.coworking.space.paymentservice.repositories.implementation;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import com.coworking.space.paymentservice.dto.mongodb.SumResult;
import com.coworking.space.paymentservice.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final MongoTemplate mongoTemplate;

    private static final String USER_ID_KEY = "user_id";
    private static final String STATUS_KEY = "status";
    private static final String ORDER_ID_KEY = "order_id";
    private static final String ID_KEY = "_id";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PAYMENT_AMOUNT_KEY = "payment_amount";
    private static final String SUM_KEY = "sum";
    private static final String PAYMENTS_COLLECTION = "payments";
    private static final double ZERO_SUM = 0.0;

    @Override
    public PaymentEntity save(PaymentEntity entity) {
        return mongoTemplate.save(entity);
    }

    @Override
    public List<PaymentEntity> findByUserId(int userId) {
        var userIdCriteria = Criteria.where(USER_ID_KEY).eq(userId);
        Query query = new Query(userIdCriteria);
        return mongoTemplate.find(query, PaymentEntity.class);
    }

    @Override
    public List<PaymentEntity> findByOrderId(int orderId) {
        var orderIdCriteria = Criteria.where(ORDER_ID_KEY).eq(orderId);
        Query query = new Query(orderIdCriteria);
        return mongoTemplate.find(query, PaymentEntity.class);
    }

    @Override
    public List<PaymentEntity> findByStatus(PaymentStatus status) {
        var statusCriteria = Criteria.where(STATUS_KEY).eq(status);
        Query query = new Query(statusCriteria);
        return mongoTemplate.find(query, PaymentEntity.class);
    }

    @Override
    public boolean existsById(ObjectId id) {
        var idCriteria = Criteria.where(ID_KEY).eq(id);
        Query query = new Query(idCriteria);
        return mongoTemplate.exists(query, Boolean.class);
    }

    @Override
    public double getSumByUser(Instant from, Instant to, int userId) {
        MatchOperation filter = Aggregation.match(
                Criteria.where(TIMESTAMP_KEY).gte(from).lte(to)
                        .and(USER_ID_KEY).eq(userId));

        return getSum(filter);
    }

    @Override
    public double getTotalSum(Instant from, Instant to) {
        MatchOperation filter = Aggregation.match(
                Criteria.where(TIMESTAMP_KEY).gte(from).lte(to));

        return getSum(filter);
    }

    private double getSum(MatchOperation matchOperation) {
        GroupOperation group = Aggregation.group().sum(PAYMENT_AMOUNT_KEY).as(SUM_KEY);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, group);
        AggregationResults<SumResult> results = mongoTemplate.aggregate(
                aggregation,
                PAYMENTS_COLLECTION,
                SumResult.class);

        SumResult sumResult = results.getUniqueMappedResult();
        return sumResult == null ? ZERO_SUM : sumResult.getSum();
    }
}
