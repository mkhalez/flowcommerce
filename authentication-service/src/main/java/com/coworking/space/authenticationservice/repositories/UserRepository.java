package com.coworking.space.authenticationservice.repositories;

import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUsername(String username);
}
