package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.CardEntity;
import com.coworking.space.userservice.repositories.projection.UserCardsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;


public interface CardRepository extends JpaRepository<CardEntity, Integer>, JpaSpecificationExecutor<CardEntity> {
    Optional<CardEntity> findById(int id);

    List<CardEntity> findByUserId(@Param("userId") int userId);

    @Modifying
    @Query(value = """
            UPDATE payment_cards
            SET active = :status
            WHERE user_id = :user_id
            """,
            nativeQuery = true)
    void updateStatusByUserId(@Param("userId") int userId);

    @Modifying
    @Query(value = """
            UPDATE payment_cards
            SET active = :status
            WHERE id = :id
            """,
            nativeQuery = true)
    void updateStatusById(@Param("id") int id, @Param("status") boolean status);

    int countByUserId(int userId);

    @Query(value = """
            SELECT COUNT(*) as count, user_id AS userId
            FROM payment_cards
            WHERE user_id in (:ids)
            GROUP BY user_id
            """,
            nativeQuery = true)
    List<UserCardsCount> getUserCardsCount(@Param("ids") List<Integer> ids);

    @Modifying
    @Query(value = """
            UPDATE payment_cards
            SET active = :status
            WHERE user_id = :user_id
            """,
            nativeQuery = true)
    void updateAllStatusById(@Param("user_id") int userId, @Param("status") boolean status);
}
