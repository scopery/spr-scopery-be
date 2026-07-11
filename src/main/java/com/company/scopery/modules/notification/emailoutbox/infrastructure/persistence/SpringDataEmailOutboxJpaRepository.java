package com.company.scopery.modules.notification.emailoutbox.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SpringDataEmailOutboxJpaRepository extends JpaRepository<EmailOutboxJpaEntity, UUID> {

    @Query("""
            SELECT o FROM EmailOutboxJpaEntity o
            WHERE o.status = 'PENDING'
              AND o.scheduledAt <= :now
            ORDER BY o.scheduledAt ASC
            """)
    List<EmailOutboxJpaEntity> findPendingBatch(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE EmailOutboxJpaEntity o
            SET o.status = 'PROCESSING', o.updatedAt = CURRENT_TIMESTAMP
            WHERE o.id = :id AND o.status = 'PENDING'
            """)
    int claimForProcessing(@Param("id") UUID id);

    @Query("""
            SELECT o FROM EmailOutboxJpaEntity o
            WHERE (:deliveryId IS NULL OR o.deliveryId = :deliveryId)
              AND (:status IS NULL OR o.status = :status)
              AND (:providerType IS NULL OR o.providerType = :providerType)
            ORDER BY o.createdAt DESC
            """)
    List<EmailOutboxJpaEntity> search(
            @Param("deliveryId") UUID deliveryId,
            @Param("status") String status,
            @Param("providerType") String providerType);

    @Query("""
            SELECT COUNT(o) FROM EmailOutboxJpaEntity o
            WHERE (:deliveryId IS NULL OR o.deliveryId = :deliveryId)
              AND (:status IS NULL OR o.status = :status)
              AND (:providerType IS NULL OR o.providerType = :providerType)
            """)
    long countSearch(
            @Param("deliveryId") UUID deliveryId,
            @Param("status") String status,
            @Param("providerType") String providerType);
}
