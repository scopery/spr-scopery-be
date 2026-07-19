package com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant; import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataReminderInstanceJpaRepository extends JpaRepository<ReminderInstanceJpaEntity, UUID> {
    Optional<ReminderInstanceJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ReminderInstanceJpaEntity> findByWorkspaceIdAndRecipientUserIdOrderByRemindAtAsc(UUID workspaceId, UUID recipientUserId);
    @Query("SELECT r FROM ReminderInstanceJpaEntity r WHERE r.status = 'PENDING' AND r.remindAt <= :now AND (r.snoozedUntil IS NULL OR r.snoozedUntil <= :now)")
    List<ReminderInstanceJpaEntity> findPendingDue(@Param("now") Instant now);
}
