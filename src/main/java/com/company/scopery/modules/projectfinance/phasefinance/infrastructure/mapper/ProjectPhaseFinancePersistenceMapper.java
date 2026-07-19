package com.company.scopery.modules.projectfinance.phasefinance.infrastructure.mapper;

import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.phasefinance.infrastructure.persistence.ProjectPhaseFinanceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectPhaseFinancePersistenceMapper {
    public ProjectPhaseFinance toDomain(ProjectPhaseFinanceJpaEntity e) {
        return new ProjectPhaseFinance(
                e.getId(), e.getFinanceScenarioId(), e.getProjectId(), e.getProjectPhaseId(),
                e.getPhaseNameSnapshot(), e.getPhaseOrder(), e.getCurrencyCode(),
                e.getEstimateHours(), e.getLaborCost(), e.getCustomCost(), e.getVendorCost(),
                e.getContingencyAmount(), e.getDirectCost(), e.getOverheadAmount(), e.getBudgetOfCosts(),
                e.getPlannedRevenue(), e.getRevenuePercent(), e.getGrossMargin(), e.getGrossMarginPercent(),
                e.getProfitBeforeTax(), e.getPbtPercent(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectPhaseFinanceJpaEntity toJpaEntity(ProjectPhaseFinance d) {
        ProjectPhaseFinanceJpaEntity e = new ProjectPhaseFinanceJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectId(d.projectId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setPhaseNameSnapshot(d.phaseNameSnapshot());
        e.setPhaseOrder(d.phaseOrder());
        e.setCurrencyCode(d.currencyCode());
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
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
