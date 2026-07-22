package com.coworking.space.userservice.repositories.util;

import com.coworking.space.userservice.domain.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SURNAME_ATTRIBUTE = "surname";
    public static Specification<UserEntity> hasName(String name){
        return (root, query, builder) ->
                name == null ? builder.conjunction() : builder.equal(root.get(NAME_ATTRIBUTE), name);
    }

    public static Specification<UserEntity> hasSurname(String surname) {
        return (root, query, builder) ->
                surname == null ? builder.conjunction() : builder.equal(root.get(SURNAME_ATTRIBUTE), surname);
    }
}
