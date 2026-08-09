package com.company.scopery.modules.finance.scenario.infrastructure.mapper;

import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;
import com.company.scopery.modules.finance.scenario.infrastructure.persistence.PhaseFinanceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PhaseFinancePersistenceMapper {

    public PhaseFinance toDomain(PhaseFinanceJpaEntity e) {
        return new PhaseFinance(
                e.getId(),
                e.getFinanceScenarioId(),
                e.getProjectPhaseId(),
                e.getPhaseNameSnapshot(),
                e.getPhaseOrder(),
                e.getEstimateHours(),
                e.getLaborCost(),
                e.getCustomCost(),
                e.getVendorCost(),
                e.getContingencyAmount(),
                e.getDirectCost(),
                e.getOverheadAmount(),
                e.getBudgetOfCosts(),
                e.getPlannedRevenue(),
                e.getRevenuePercent(),
                e.getGrossMargin(),
                e.getGrossMarginPercent(),
                e.getProfitBeforeTax(),
                e.getPbtPercent(),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public PhaseFinanceJpaEntity toJpaEntity(PhaseFinance d) {
        PhaseFinanceJpaEntity e = new PhaseFinanceJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setPhaseNameSnapshot(d.phaseNameSnapshot());
        e.setPhaseOrder(d.phaseOrder() != null ? d.phaseOrder() : 0);
        e.setEstimateHours(d.estimateHours());
        e.setLaborCost(d.laborCost());
        e.setCustomCost(d.customCost());
        e.setVendorCost(d.vendorCost());
        e.setContingencyAmount(d.contingencyAmount());
        e.setDirectCost(d.directCost());
        e.setOverheadAmount(d.overheadAmount());
        e.setBudgetOfCosts(d.budgetOfCosts());
        e.setPlannedRevenue(d.plannedRevenue());
        e.setRevenuePercent(d.revenuePercent());
        e.setGrossMargin(d.grossMargin());
        e.setGrossMarginPercent(d.grossMarginPercent());
        e.setProfitBeforeTax(d.profitBeforeTax());
        e.setPbtPercent(d.pbtPercent());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
