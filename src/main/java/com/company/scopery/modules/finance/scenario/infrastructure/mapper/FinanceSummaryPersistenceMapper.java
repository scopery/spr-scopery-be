package com.company.scopery.modules.finance.scenario.infrastructure.mapper;

import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummary;
import com.company.scopery.modules.finance.scenario.infrastructure.persistence.FinanceSummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FinanceSummaryPersistenceMapper {

    public FinanceSummary toDomain(FinanceSummaryJpaEntity e) {
        return new FinanceSummary(
                e.getId(),
                e.getFinanceScenarioId(),
                e.getProjectId(),
                e.getCurrencyCode(),
                e.getTotalEstimateHours(),
                e.getTotalLaborCost(),
                e.getTotalCustomCost(),
                e.getTotalVendorCost(),
                e.getTotalContingency(),
                e.getTotalDirectCost(),
                e.getTotalOverhead(),
                e.getBudgetOfCosts(),
                e.getPlannedRevenue(),
                e.getGrossMargin(),
                e.getGrossMarginPercent(),
                e.getProfitBeforeTax(),
                e.getPbtPercent(),
                e.getAverageCostRate(),
                e.getFormulaVersion(),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public FinanceSummaryJpaEntity toJpaEntity(FinanceSummary d) {
        FinanceSummaryJpaEntity e = new FinanceSummaryJpaEntity();
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
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
