package com.company.scopery.modules.profitability.variance.infrastructure.mapper;

import com.company.scopery.modules.profitability.variance.domain.model.ProfitVariance;
import com.company.scopery.modules.profitability.variance.infrastructure.persistence.ProfitVarianceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitVariancePersistenceMapper {

    public ProfitVariance toDomain(ProfitVarianceJpaEntity e) {
        return new ProfitVariance(
                e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getProfitabilityProfileId(), e.getVarianceType(),
                e.getFromAmount(), e.getToAmount(), e.getVarianceAmount(), e.getVariancePercent(),
                e.getCurrency(), e.getExplanation(), e.getSourceSnapshotId(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt());
    }

    public ProfitVarianceJpaEntity toJpa(ProfitVariance d) {
        ProfitVarianceJpaEntity e = new ProfitVarianceJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfitabilityProfileId(d.profitabilityProfileId());
        e.setVarianceType(d.varianceType());
        e.setFromAmount(d.fromAmount());
        e.setToAmount(d.toAmount());
        e.setVarianceAmount(d.varianceAmount());
        e.setVariancePercent(d.variancePercent());
        e.setCurrency(d.currency());
        e.setExplanation(d.explanation());
        e.setSourceSnapshotId(d.sourceSnapshotId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
