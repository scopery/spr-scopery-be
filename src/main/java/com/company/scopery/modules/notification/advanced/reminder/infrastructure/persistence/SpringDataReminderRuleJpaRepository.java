package com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataReminderRuleJpaRepository extends JpaRepository<ReminderRuleJpaEntity, UUID> {
    Optional<ReminderRuleJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ReminderRuleJpaEntity> findByWorkspaceIdOrderByRuleCodeAsc(UUID workspaceId);
    List<ReminderRuleJpaEntity> findByWorkspaceIdAndStatusOrderByRuleCodeAsc(UUID workspaceId, String status);
}
