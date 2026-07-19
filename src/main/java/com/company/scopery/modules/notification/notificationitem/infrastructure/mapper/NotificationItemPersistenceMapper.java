package com.company.scopery.modules.notification.notificationitem.infrastructure.mapper;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.infrastructure.persistence.NotificationItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationItemPersistenceMapper {

    public NotificationItem toDomain(NotificationItemJpaEntity e) {
        return NotificationItem.reconstitute(
                e.getId(), e.getRecipientUserId(), e.getEventDefinitionId(),
                e.getSourceSystem(), e.getSourceResourceType(), e.getSourceResourceId(),
                e.getOrganizationId(), e.getWorkspaceId(), e.getProjectId(),
                e.getTitle(), e.getBodyPreview(),
                NotificationSeverity.valueOf(e.getSeverity()),
                NotificationPriority.valueOf(e.getPriority()),
                e.getActionType(), e.getActionUrl(), e.getDedupKey(), e.isMandatory(),
                NotificationItemStatus.valueOf(e.getStatus()),
                e.getReadAt(), e.getDismissedAt(), e.getTraceId(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public NotificationItemJpaEntity toJpaEntity(NotificationItem d) {
        NotificationItemJpaEntity e = new NotificationItemJpaEntity();
        e.setId(d.id());
        e.setRecipientUserId(d.recipientUserId());
        e.setEventDefinitionId(d.eventDefinitionId());
        e.setSourceSystem(d.sourceSystem());
        e.setSourceResourceType(d.sourceResourceType());
        e.setSourceResourceId(d.sourceResourceId());
        e.setOrganizationId(d.organizationId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setTitle(d.title());
        e.setBodyPreview(d.bodyPreview());
        e.setSeverity(d.severity().name());
        e.setPriority(d.priority().name());
        e.setActionType(d.actionType());
        e.setActionUrl(d.actionUrl());
        e.setDedupKey(d.dedupKey());
        e.setMandatory(d.mandatory());
        e.setStatus(d.status().name());
        e.setReadAt(d.readAt());
        e.setDismissedAt(d.dismissedAt());
        e.setTraceId(d.traceId());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
