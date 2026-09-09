package com.coworking.space.paymentservice.repositories.implementation;

import com.coworking.space.paymentservice.domain.entities.PaymentEventEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentEventStatus;
import com.coworking.space.paymentservice.infrastructure.properties.PaymentSenderProperties;
import com.coworking.space.paymentservice.repositories.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentEventRepositoryImpl implements PaymentEventRepository {
    private final MongoTemplate mongoTemplate;
    private final PaymentSenderProperties paymentSenderProperties;

    private static final String EVENT_STATUS_KEY = "event_status";
    private static final String SENDING_STARTED_AT_KEY = "sending_started_at";
    private static final String ATTEMPT_COUNT_KEY = "attempt_count";
    private static final String NEXT_ATTEMPT_AT_KEY = "next_attempt_at";
    private static final String CREATED_AT_KEY = "created_at";
    private static final String ID_KEY = "_id";
    private static final int INCREMENT_BY_ONE = 1;

    @Override
    public PaymentEventEntity save(PaymentEventEntity entity) {
        return mongoTemplate.save(entity);
    }

    @Override
    public List<PaymentEventEntity> entitiesToSend() {
        Instant now = Instant.now();
        Instant sendingTimeoutThreshold = now.minus(paymentSenderProperties.getTimeToSendMinutes(), ChronoUnit.MINUTES);

        Criteria createdBranch = Criteria.where(EVENT_STATUS_KEY).is(PaymentEventStatus.CREATED)
                .and(NEXT_ATTEMPT_AT_KEY).lt(now);

        Criteria pendingBranch = Criteria.where(EVENT_STATUS_KEY).is(PaymentEventStatus.PENDING)
                .and(SENDING_STARTED_AT_KEY).lte(sendingTimeoutThreshold);

        Criteria statusOr = new Criteria().orOperator(createdBranch, pendingBranch);

        Criteria attemptCountFilter = Criteria.where(ATTEMPT_COUNT_KEY).lte(paymentSenderProperties.getMaxAttempts());
        Criteria fullFilter = new Criteria().andOperator(attemptCountFilter, statusOr);

        Query query = new Query(fullFilter)
                .with(Sort.by(Sort.Direction.ASC, CREATED_AT_KEY))
                .limit(paymentSenderProperties.getLimit());

        List<PaymentEventEntity> candidates = mongoTemplate.find(query, PaymentEventEntity.class);

        if (candidates.isEmpty()) {
            return List.of();
        }

        int maxAttempts = paymentSenderProperties.getMaxAttempts();

        List<ObjectId> idsToFail = candidates.stream()
                .filter(e -> e.getAttemptCount() >= maxAttempts)
                .map(PaymentEventEntity::getId)
                .toList();

        List<ObjectId> idsToSend = candidates.stream()
                .filter(e -> e.getAttemptCount() < maxAttempts)
                .map(PaymentEventEntity::getId)
                .toList();

        if (!idsToFail.isEmpty()) {
            Criteria failIdsCriteria = Criteria.where(ID_KEY).in(idsToFail);
            Criteria failIdsWithStatusCriteria = new Criteria().andOperator(failIdsCriteria, statusOr);
            Query queryToFail = new Query(failIdsWithStatusCriteria);
            Update updateToFail = new Update().set(EVENT_STATUS_KEY, PaymentEventStatus.FAIL_TO_SEND);
            mongoTemplate.updateMulti(queryToFail, updateToFail, PaymentEventEntity.class);
        }

        if (idsToSend.isEmpty()) {
            return List.of();
        }

        Criteria sendIdsCriteria = Criteria.where(ID_KEY).in(idsToSend);
        Criteria sendIdsWithStatusCriteria = new Criteria().andOperator(sendIdsCriteria, statusOr);
        Query queryToSend = new Query(sendIdsWithStatusCriteria);
        Update updateToSend = new Update()
                .set(EVENT_STATUS_KEY, PaymentEventStatus.PENDING)
                .set(SENDING_STARTED_AT_KEY, now)
                .inc(ATTEMPT_COUNT_KEY, INCREMENT_BY_ONE);
        mongoTemplate.updateMulti(queryToSend, updateToSend, PaymentEventEntity.class);

        Criteria reallyUpdatedFilter = Criteria.where(ID_KEY).in(idsToSend)
                .and(SENDING_STARTED_AT_KEY).is(now);

        Query reallyUpdatedQuery = new Query(reallyUpdatedFilter);

        return mongoTemplate.find(reallyUpdatedQuery, PaymentEventEntity.class);
    }

    @Override
    public Optional<PaymentEventEntity> findById(ObjectId id) {
        Criteria criteria = Criteria.where(ID_KEY).is(id);
        Query query = new Query(criteria);
        return Optional.ofNullable(mongoTemplate.findOne(query, PaymentEventEntity.class));
    }
}
