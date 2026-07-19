package com.company.scopery.modules.profitability.plan.infrastructure.mapper;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersion;
import com.company.scopery.modules.profitability.plan.infrastructure.persistence.ProfitabilityPlanVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitabilityPlanVersionPersistenceMapper {

    public ProfitabilityPlanVersion toDomain(ProfitabilityPlanVersionJpaEntity e) {
        return new ProfitabilityPlanVersion(
                e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getProfitabilityPlanId(), e.getVersionNumber(), e.getVersionLabel(),
                e.getCurrency(),
                e.getBaselineRevenue(), e.getBaselineCost(), e.getBaselineProfit(), e.getBaselineMarginPercent(),
                e.getPlannedRevenue(), e.getPlannedCost(), e.getPlannedProfit(), e.getPlannedMarginPercent(),
                e.getAssumptionNotes(), e.getSourceQuoteVersionId(), e.getSourceBaselineId(),
                e.isFinalizedFlag(), e.getFinalizedAt(), e.getFinalizedBy(),
                e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProfitabilityPlanVersionJpaEntity toJpa(ProfitabilityPlanVersion d) {
        ProfitabilityPlanVersionJpaEntity e = new ProfitabilityPlanVersionJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfitabilityPlanId(d.profitabilityPlanId());
        e.setVersionNumber(d.versionNumber());
        e.setVersionLabel(d.versionLabel());
        e.setCurrency(d.currency());
        e.setBaselineRevenue(d.baselineRevenue());
        e.setBaselineCost(d.baselineCost());
        e.setBaselineProfit(d.baselineProfit());
        e.setBaselineMarginPercent(d.baselineMarginPercent());
        e.setPlannedRevenue(d.plannedRevenue());
        e.setPlannedCost(d.plannedCost());
        e.setPlannedProfit(d.plannedProfit());
        e.setPlannedMarginPercent(d.plannedMarginPercent());
        e.setAssumptionNotes(d.assumptionNotes());
        e.setSourceQuoteVersionId(d.sourceQuoteVersionId());
        e.setSourceBaselineId(d.sourceBaselineId());
        e.setFinalizedFlag(d.finalizedFlag());
        e.setFinalizedAt(d.finalizedAt());
        e.setFinalizedBy(d.finalizedBy());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
