package com.company.scopery.modules.notification.advanced.suppression.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionEntry;
import com.company.scopery.modules.notification.advanced.suppression.infrastructure.persistence.NotificationSuppressionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NotificationSuppressionPersistenceMapper {
    public NotificationSuppressionEntry toDomain(NotificationSuppressionJpaEntity e) {
        return new NotificationSuppressionEntry(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getUserId(), e.getCategoryCode(),
                e.getChannelCode(), e.getReasonCode(), e.getSourceType(), e.getSourceId(), e.getSuppressedAt(), e.getExpiresAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public NotificationSuppressionJpaEntity toJpaEntity(NotificationSuppressionEntry d) {
        var e = new NotificationSuppressionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setUserId(d.userId());
        e.setCategoryCode(d.categoryCode()); e.setChannelCode(d.channelCode()); e.setReasonCode(d.reasonCode());
        e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId()); e.setSuppressedAt(d.suppressedAt()); e.setExpiresAt(d.expiresAt());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
