package com.company.scopery.modules.profitability.plan.infrastructure.mapper;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlan;
import com.company.scopery.modules.profitability.plan.infrastructure.persistence.ProfitabilityPlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitabilityPlanPersistenceMapper {

    public ProfitabilityPlan toDomain(ProfitabilityPlanJpaEntity e) {
        return new ProfitabilityPlan(
                e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getProfitabilityProfileId(), e.getPlanCode(), e.getName(), e.getPlanType(),
                e.getStatus(), e.getCurrentVersionId(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProfitabilityPlanJpaEntity toJpa(ProfitabilityPlan d) {
        ProfitabilityPlanJpaEntity e = new ProfitabilityPlanJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfitabilityProfileId(d.profitabilityProfileId());
        e.setPlanCode(d.planCode());
        e.setName(d.name());
        e.setPlanType(d.planType());
        e.setStatus(d.status());
        e.setCurrentVersionId(d.currentVersionId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
