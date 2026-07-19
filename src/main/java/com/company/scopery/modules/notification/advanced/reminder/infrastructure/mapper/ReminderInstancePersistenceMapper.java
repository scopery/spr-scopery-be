package com.company.scopery.modules.notification.advanced.reminder.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderInstance;
import com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence.ReminderInstanceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReminderInstancePersistenceMapper {
    public ReminderInstance toDomain(ReminderInstanceJpaEntity e) {
        return new ReminderInstance(e.getId(), e.getWorkspaceId(), e.getReminderRuleId(), e.getSourceType(), e.getSourceId(),
                e.getRecipientUserId(), e.getRemindAt(), e.getStatus(), e.getDedupKey(),
                e.getSnoozedUntil(), e.getDismissedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReminderInstanceJpaEntity toJpa(ReminderInstance d) {
        var e = new ReminderInstanceJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setReminderRuleId(d.reminderRuleId());
        e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId()); e.setRecipientUserId(d.recipientUserId());
        e.setRemindAt(d.remindAt()); e.setStatus(d.status()); e.setDedupKey(d.dedupKey());
        e.setSnoozedUntil(d.snoozedUntil()); e.setDismissedAt(d.dismissedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
