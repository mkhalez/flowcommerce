package com.coworking.space.orderservice.repositories.specification;

import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.domain.enums.Status;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class OrderSpecification {
    private static final String STATUS_FILED = "status";
    private static final String CREATE_AT_FIELD = "createdAt";

    public Specification<OrderEntity> hasStatus(Status status) {
        return (root, query, builder) ->
            status == null
                    ? builder.conjunction()
                    : builder.equal(root.get(STATUS_FILED), status);
    }

    public Specification<OrderEntity> createFrom(OffsetDateTime from) {
        return  (root, query, builder) ->
                from == null
                        ? builder.conjunction()
                        : builder.greaterThanOrEqualTo((root.get(CREATE_AT_FIELD)), from.toInstant());
    }

    public Specification<OrderEntity> createTo(OffsetDateTime to) {
        return  (root, query, builder) ->
                to == null
                        ? builder.conjunction()
                        : builder.lessThanOrEqualTo((root.get(CREATE_AT_FIELD)), to.toInstant());
    }

}
