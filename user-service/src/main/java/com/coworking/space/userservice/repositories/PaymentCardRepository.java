package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, Integer>, JpaSpecificationExecutor<PaymentCardEntity> {
    PaymentCardEntity findById(int id);

    @Query("""
            SELECT p
            FROM PaymentCardEntity p
            WHERE p.user.id = :userId
        """)
    List<PaymentCardEntity> findByUserId(@Param("userId") int userId, Pageable pageable);

    @Modifying
    @Query(value = """
            UPDATE payment_cards
            SET active = :status
            WHERE user_id = :user_id
            """, nativeQuery = true)
    void updateStatusByUserId(@Param("userId") int userId);

    @Modifying
    @Query(value = """
            UPDATE payment_cards
            SET active = :status
            WHERE id = :id
            """, nativeQuery = true)
    void updateStatusById(@Param("userId") int userId);

    int countByUserId(int userId);
}
