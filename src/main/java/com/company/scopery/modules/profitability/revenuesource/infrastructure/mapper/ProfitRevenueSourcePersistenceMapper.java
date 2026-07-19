package com.company.scopery.modules.profitability.revenuesource.infrastructure.mapper;

import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSource;
import com.company.scopery.modules.profitability.revenuesource.infrastructure.persistence.ProfitRevenueSourceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitRevenueSourcePersistenceMapper {
    public ProfitRevenueSource toDomain(ProfitRevenueSourceJpaEntity e) {
        return new ProfitRevenueSource(
                e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getProfileId(), e.getSourceType(), e.getSourceId(),
                e.getAmount(), e.getCurrency(), e.isIncludedInForecast(), e.getConfidence(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProfitRevenueSourceJpaEntity toJpa(ProfitRevenueSource d) {
        ProfitRevenueSourceJpaEntity e = new ProfitRevenueSourceJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfileId(d.profileId());
        e.setSourceType(d.sourceType());
        e.setSourceId(d.sourceId());
        e.setAmount(d.amount());
        e.setCurrency(d.currency());
        e.setIncludedInForecast(d.includedInForecast());
        e.setConfidence(d.confidence());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
