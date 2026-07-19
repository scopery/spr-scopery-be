package com.company.scopery.modules.projectfinance.summary.infrastructure.mapper;

import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.infrastructure.persistence.ProjectFinanceSummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectFinanceSummaryPersistenceMapper {
    public ProjectFinanceSummary toDomain(ProjectFinanceSummaryJpaEntity e) {
        return new ProjectFinanceSummary(
                e.getId(), e.getFinanceScenarioId(), e.getProjectId(), e.getCurrencyCode(),
                e.getTotalEstimateHours(), e.getTotalLaborCost(), e.getTotalCustomCost(),
                e.getTotalVendorCost(), e.getTotalContingency(), e.getTotalDirectCost(),
                e.getTotalOverhead(), e.getBudgetOfCosts(), e.getPlannedRevenue(),
                e.getGrossMargin(), e.getGrossMarginPercent(), e.getProfitBeforeTax(), e.getPbtPercent(),
                e.getAverageCostRate(), e.getFormulaVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectFinanceSummaryJpaEntity toJpaEntity(ProjectFinanceSummary d) {
        ProjectFinanceSummaryJpaEntity e = new ProjectFinanceSummaryJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectId(d.projectId());
        e.setCurrencyCode(d.currencyCode());
        e.setTotalEstimateHours(d.totalEstimateHours());
        e.setTotalLaborCost(d.totalLaborCost());
        e.setTotalCustomCost(d.totalCustomCost());
        e.setTotalVendorCost(d.totalVendorCost());
        e.setTotalContingency(d.totalContingency());
        e.setTotalDirectCost(d.totalDirectCost());
        e.setTotalOverhead(d.totalOverhead());
        e.setBudgetOfCosts(d.budgetOfCosts());
        e.setPlannedRevenue(d.plannedRevenue());
        e.setGrossMargin(d.grossMargin());
        e.setGrossMarginPercent(d.grossMarginPercent());
        e.setProfitBeforeTax(d.profitBeforeTax());
        e.setPbtPercent(d.pbtPercent());
        e.setAverageCostRate(d.averageCostRate());
        e.setFormulaVersion(d.formulaVersion());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
