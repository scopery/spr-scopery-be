package com.company.scopery.modules.profitability.riskflag.infrastructure.mapper;

import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlag;
import com.company.scopery.modules.profitability.riskflag.infrastructure.persistence.ProfitRiskFlagJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitRiskFlagPersistenceMapper {

    public ProfitRiskFlag toDomain(ProfitRiskFlagJpaEntity e) {
        return new ProfitRiskFlag(
                e.getId(),
                e.getWorkspaceId(),
                e.getProjectId(),
                e.getReason(),
                e.getImpactType(),
                e.getAmountAtRisk(),
                e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public ProfitRiskFlagJpaEntity toJpa(ProfitRiskFlag d) {
        ProfitRiskFlagJpaEntity e = new ProfitRiskFlagJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setReason(d.reason());
        e.setImpactType(d.impactType());
        e.setAmountAtRisk(d.amountAtRisk());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
