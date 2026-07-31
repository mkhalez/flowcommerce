package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import com.coworking.space.authenticationservice.domain.models.Role;
import com.coworking.space.authenticationservice.domain.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    User toUser(UserEntity userEntity, Set<Role> roles);
}
