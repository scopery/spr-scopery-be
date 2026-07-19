package com.company.scopery.modules.trust.evidence.infrastructure.mapper;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecord;
import com.company.scopery.modules.trust.evidence.infrastructure.persistence.ComplianceEvidenceRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ComplianceEvidencePersistenceMapper {
    public ComplianceEvidenceRecordJpaEntity toJpaEntity(ComplianceEvidenceRecord d) {
        ComplianceEvidenceRecordJpaEntity e = new ComplianceEvidenceRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setEvidenceCode(d.evidenceCode());
        e.setEvidenceType(d.evidenceType()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setStatus(d.status()); e.setOwnerUserId(d.ownerUserId()); e.setEvidenceDate(d.evidenceDate());
        e.setFinalizedAt(d.finalizedAt()); e.setFinalizedBy(d.finalizedBy()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public ComplianceEvidenceRecord toDomain(ComplianceEvidenceRecordJpaEntity e) {
        return new ComplianceEvidenceRecord(e.getId(), e.getWorkspaceId(), e.getEvidenceCode(), e.getEvidenceType(),
                e.getTitle(), e.getDescription(), e.getStatus(), e.getOwnerUserId(), e.getEvidenceDate(),
                e.getFinalizedAt(), e.getFinalizedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
