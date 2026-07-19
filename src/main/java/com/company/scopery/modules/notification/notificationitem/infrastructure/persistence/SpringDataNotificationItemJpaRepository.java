package com.company.scopery.modules.notification.notificationitem.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataNotificationItemJpaRepository extends JpaRepository<NotificationItemJpaEntity, UUID> {

    boolean existsByRecipientUserIdAndDedupKey(UUID recipientUserId, String dedupKey);

    @Query("""
            SELECT n FROM NotificationItemJpaEntity n
            WHERE n.recipientUserId = :recipientUserId
              AND (:includeDismissed = true OR n.status <> 'DISMISSED')
            ORDER BY n.createdAt DESC
            """)
    List<NotificationItemJpaEntity> findByRecipient(
            @Param("recipientUserId") UUID recipientUserId,
            @Param("includeDismissed") boolean includeDismissed,
            Pageable pageable);

    @Query("""
            SELECT COUNT(n) FROM NotificationItemJpaEntity n
            WHERE n.recipientUserId = :recipientUserId
              AND (:includeDismissed = true OR n.status <> 'DISMISSED')
            """)
    long countByRecipient(
            @Param("recipientUserId") UUID recipientUserId,
            @Param("includeDismissed") boolean includeDismissed);

    @Query("""
            SELECT COUNT(n) FROM NotificationItemJpaEntity n
            WHERE n.recipientUserId = :recipientUserId
              AND n.status = 'UNREAD'
            """)
    long countUnread(@Param("recipientUserId") UUID recipientUserId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE NotificationItemJpaEntity n
            SET n.status = 'READ',
                n.readAt = CURRENT_TIMESTAMP,
                n.updatedAt = CURRENT_TIMESTAMP
            WHERE n.recipientUserId = :recipientUserId
              AND n.status = 'UNREAD'
            """)
    int markAllRead(@Param("recipientUserId") UUID recipientUserId);
}
