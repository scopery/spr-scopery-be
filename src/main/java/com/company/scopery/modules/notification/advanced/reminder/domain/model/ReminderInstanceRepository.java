package com.company.scopery.modules.notification.advanced.reminder.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ReminderInstanceRepository {
    ReminderInstance save(ReminderInstance r);
    Optional<ReminderInstance> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ReminderInstance> findByWorkspaceIdAndRecipientUserId(UUID workspaceId, UUID recipientUserId);
    List<ReminderInstance> findPendingDue(java.time.Instant now);
}
