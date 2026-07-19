package com.company.scopery.modules.notification.advanced.reminder.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ReminderRuleRepository {
    ReminderRule save(ReminderRule r);
    Optional<ReminderRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ReminderRule> findByWorkspaceId(UUID workspaceId);
    List<ReminderRule> findActiveByWorkspaceId(UUID workspaceId);
}
