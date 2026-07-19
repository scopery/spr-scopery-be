package com.company.scopery.modules.trust.anonymization.infrastructure.mapper;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlan;
import com.company.scopery.modules.trust.anonymization.infrastructure.persistence.AnonymizationPlanJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AnonymizationPlanPersistenceMapper {
    public AnonymizationPlanJpaEntity toJpaEntity(AnonymizationPlan d) {
        AnonymizationPlanJpaEntity e = new AnonymizationPlanJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setDataSubjectIndexId(d.dataSubjectIndexId());
        e.setStatus(d.status()); e.setPlanJson(d.planJson()); e.setDryRunResultJson(d.dryRunResultJson());
        e.setLegalHoldBlocked(d.legalHoldBlocked()); e.setReason(d.reason());
        e.setExecutedAt(d.executedAt()); e.setCancelledAt(d.cancelledAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public AnonymizationPlan toDomain(AnonymizationPlanJpaEntity e) {
        return new AnonymizationPlan(e.getId(), e.getWorkspaceId(), e.getDataSubjectIndexId(),
                e.getStatus(), e.getPlanJson(), e.getDryRunResultJson(), e.isLegalHoldBlocked(),
                e.getReason(), e.getExecutedAt(), e.getCancelledAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
