package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.domain.entities.RoleEntity;
import com.coworking.space.authenticationservice.domain.models.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleEntity roleEntity);
}
