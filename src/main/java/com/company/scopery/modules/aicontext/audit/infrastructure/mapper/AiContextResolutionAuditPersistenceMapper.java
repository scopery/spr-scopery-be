package com.company.scopery.modules.aicontext.audit.infrastructure.mapper;

import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAudit;
import com.company.scopery.modules.aicontext.audit.infrastructure.persistence.AiContextResolutionAuditJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiContextResolutionAuditPersistenceMapper {

    public AiContextResolutionAuditJpaEntity toJpaEntity(AiContextResolutionAudit audit) {
        AiContextResolutionAuditJpaEntity entity = new AiContextResolutionAuditJpaEntity();
        entity.setId(audit.id());
        entity.setPolicyId(audit.policyId());
        entity.setDocumentId(audit.documentId());
        entity.setActorId(audit.actorId());
        entity.setTokenCount(audit.tokenCount());
        entity.setBlockCount(audit.blockCount());
        entity.setStatus(audit.status());
        entity.setErrorMessage(audit.errorMessage());
        entity.setResolvedAt(audit.resolvedAt());
        return entity;
    }

    public AiContextResolutionAudit toDomain(AiContextResolutionAuditJpaEntity entity) {
        return new AiContextResolutionAudit(
                entity.getId(),
                entity.getPolicyId(),
                entity.getDocumentId(),
                entity.getActorId(),
                entity.getTokenCount(),
                entity.getBlockCount(),
                entity.getStatus(),
                entity.getErrorMessage(),
                entity.getResolvedAt()
        );
    }
}
