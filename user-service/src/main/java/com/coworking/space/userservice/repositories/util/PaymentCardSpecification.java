package com.coworking.space.userservice.repositories.util;

import com.coworking.space.userservice.domain.entities.PaymentCardEntity;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {
    private static final String HOLDER_SURNAME_ATTRIBUTE = "holder";
    public static Specification<PaymentCardEntity> hasName(String name, String surname){
        return (root, query, builder) ->
                name == null || surname == null ? builder.conjunction() : builder.equal(root.get(HOLDER_SURNAME_ATTRIBUTE), getHolder(name, surname));
    }

    private static String getHolder(String name, String surname) {
        return name.toUpperCase() + " " + surname.toUpperCase();
    }

}
