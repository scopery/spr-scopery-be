package com.company.scopery.modules.scope.evidence.infrastructure.mapper;
import com.company.scopery.modules.scope.evidence.domain.enums.EvidenceType;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidence;
import com.company.scopery.modules.scope.evidence.infrastructure.persistence.AcceptanceEvidenceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AcceptanceEvidencePersistenceMapper {
    public AcceptanceEvidence toDomain(AcceptanceEvidenceJpaEntity e) {
        return new AcceptanceEvidence(e.getId(), e.getDeliverableId(), e.getAcceptanceCriteriaId(), e.getProjectId(),
                EvidenceType.valueOf(e.getEvidenceType()), e.getTitle(), e.getContentText(), e.getLinkUrl(),
                e.getReferenceId(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public AcceptanceEvidenceJpaEntity toJpaEntity(AcceptanceEvidence d) {
        AcceptanceEvidenceJpaEntity e = new AcceptanceEvidenceJpaEntity();
        e.setId(d.id()); e.setDeliverableId(d.deliverableId()); e.setAcceptanceCriteriaId(d.acceptanceCriteriaId());
        e.setProjectId(d.projectId()); e.setEvidenceType(d.evidenceType().name()); e.setTitle(d.title());
        e.setContentText(d.contentText()); e.setLinkUrl(d.linkUrl()); e.setReferenceId(d.referenceId());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
