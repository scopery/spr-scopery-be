package com.company.scopery.modules.clientportal.auditlog.infrastructure.mapper;
import com.company.scopery.modules.clientportal.auditlog.domain.model.ExternalPortalAuditLog;
import com.company.scopery.modules.clientportal.auditlog.infrastructure.persistence.ExternalPortalAuditLogJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalPortalAuditLogPersistenceMapper {
    public ExternalPortalAuditLog toDomain(ExternalPortalAuditLogJpaEntity e) {
        return new ExternalPortalAuditLog(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getPortalAccountId(),
                e.getAction(), e.getTargetType(), e.getTargetId(), e.getDetails(),
                e.getOccurredAt(), e.getIpAddress(), e.getCreatedAt());
    }
    public ExternalPortalAuditLogJpaEntity toJpaEntity(ExternalPortalAuditLog d) {
        ExternalPortalAuditLogJpaEntity e = new ExternalPortalAuditLogJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setPortalAccountId(d.portalAccountId()); e.setAction(d.action()); e.setTargetType(d.targetType());
        e.setTargetId(d.targetId()); e.setDetails(d.details()); e.setOccurredAt(d.occurredAt());
        e.setIpAddress(d.ipAddress());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
