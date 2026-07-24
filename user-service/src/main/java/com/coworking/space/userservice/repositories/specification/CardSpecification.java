package com.coworking.space.userservice.repositories.specification;

import com.coworking.space.userservice.domain.entities.CardEntity;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {
    private static final String HOLDER_SURNAME_ATTRIBUTE = "holder";
    public static Specification<CardEntity> hasHolder(String holder){
        return (root, query, builder) ->
                holder == null ? builder.conjunction() : builder.equal(root.get(HOLDER_SURNAME_ATTRIBUTE), holder);
    }

}
