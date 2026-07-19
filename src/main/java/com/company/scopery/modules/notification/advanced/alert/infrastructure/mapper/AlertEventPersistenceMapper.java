package com.company.scopery.modules.notification.advanced.alert.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertEvent;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence.AlertEventJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AlertEventPersistenceMapper {
    public AlertEvent toDomain(AlertEventJpaEntity e) {
        return new AlertEvent(e.getId(), e.getWorkspaceId(), e.getAlertRuleId(), e.getSourceType(), e.getSourceId(),
                e.getSeverity(), e.getTitle(), e.getStatus(), e.getDedupKey(),
                e.getAcknowledgedAt(), e.getDismissedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public AlertEventJpaEntity toJpa(AlertEvent d) {
        var e = new AlertEventJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setAlertRuleId(d.alertRuleId());
        e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId()); e.setSeverity(d.severity());
        e.setTitle(d.title()); e.setStatus(d.status()); e.setDedupKey(d.dedupKey());
        e.setAcknowledgedAt(d.acknowledgedAt()); e.setDismissedAt(d.dismissedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
