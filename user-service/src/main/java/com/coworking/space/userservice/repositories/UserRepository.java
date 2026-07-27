package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findById(int id);

    @Modifying
    @Query(value = """
            UPDATE users
            SET active = :status
            WHERE id = :id
            """, nativeQuery = true)
    void updateStatusById(@Param("id") int id, @Param("status") boolean status);
}
