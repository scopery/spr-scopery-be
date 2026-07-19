package com.company.scopery.modules.profitability.costsource.infrastructure.mapper;

import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSource;
import com.company.scopery.modules.profitability.costsource.infrastructure.persistence.ProfitCostSourceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitCostSourcePersistenceMapper {
    public ProfitCostSource toDomain(ProfitCostSourceJpaEntity e) {
        return new ProfitCostSource(
                e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getProfileId(), e.getSourceType(), e.getSourceId(),
                e.getEffortHours(), e.getRateAmount(), e.getAmount(), e.getCurrency(), e.isIncludedInForecast(),
                e.getStatus(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProfitCostSourceJpaEntity toJpa(ProfitCostSource d) {
        ProfitCostSourceJpaEntity e = new ProfitCostSourceJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfileId(d.profileId());
        e.setSourceType(d.sourceType());
        e.setSourceId(d.sourceId());
        e.setEffortHours(d.effortHours());
        e.setRateAmount(d.rateAmount());
        e.setAmount(d.amount());
        e.setCurrency(d.currency());
        e.setIncludedInForecast(d.includedInForecast());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
