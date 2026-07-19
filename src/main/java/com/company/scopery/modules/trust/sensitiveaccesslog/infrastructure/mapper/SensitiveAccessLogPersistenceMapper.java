package com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.mapper;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLog;
import com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.persistence.SensitiveAccessLogJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SensitiveAccessLogPersistenceMapper {
    public SensitiveAccessLogJpaEntity toJpaEntity(SensitiveAccessLog d) {
        SensitiveAccessLogJpaEntity e = new SensitiveAccessLogJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setActorPrincipalType(d.actorPrincipalType()); e.setActorUserId(d.actorUserId());
        e.setTargetObjectType(d.targetObjectType()); e.setTargetObjectId(d.targetObjectId());
        e.setFieldPath(d.fieldPath()); e.setClassification(d.classification());
        e.setAccessAction(d.accessAction()); e.setAccessChannel(d.accessChannel());
        e.setReason(d.reason()); e.setRequestPath(d.requestPath());
        e.setOccurredAt(d.occurredAt()); e.setTraceId(d.traceId());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SensitiveAccessLog toDomain(SensitiveAccessLogJpaEntity e) {
        return new SensitiveAccessLog(e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getActorPrincipalType(), e.getActorUserId(), e.getTargetObjectType(), e.getTargetObjectId(),
                e.getFieldPath(), e.getClassification(), e.getAccessAction(), e.getAccessChannel(),
                e.getReason(), e.getRequestPath(), e.getOccurredAt(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
