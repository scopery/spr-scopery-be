package com.company.scopery.modules.traceability.specdocrevision.infrastructure.mapper;

import com.company.scopery.modules.traceability.specdocrevision.domain.enums.RegistrySpecDocRevisionStatus;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevision;
import com.company.scopery.modules.traceability.specdocrevision.infrastructure.persistence.RegistrySpecDocRevisionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistrySpecDocRevisionPersistenceMapper {

    public RegistrySpecDocRevision toDomain(RegistrySpecDocRevisionJpaEntity e) {
        return new RegistrySpecDocRevision(
                e.getId(),
                e.getDocumentId(),
                e.getWorkspaceId(),
                e.getRevisionNo(),
                e.getTargetSheetName(),
                e.getDetails(),
                e.getPersonInCharge(),
                e.getColor(),
                e.getChangedAt(),
                e.getDisplayOrder(),
                RegistrySpecDocRevisionStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistrySpecDocRevisionJpaEntity toJpaEntity(RegistrySpecDocRevision d) {
        RegistrySpecDocRevisionJpaEntity e = new RegistrySpecDocRevisionJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setRevisionNo(d.revisionNo());
        e.setTargetSheetName(d.targetSheetName());
        e.setDetails(d.details());
        e.setPersonInCharge(d.personInCharge());
        e.setColor(d.color());
        e.setChangedAt(d.changedAt());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
