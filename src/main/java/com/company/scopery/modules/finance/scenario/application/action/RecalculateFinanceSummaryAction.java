package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.domain.enums.CostAdjustmentMethod;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCostRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummary;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummaryRepository;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinanceRepository;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCostRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RecalculateFinanceSummaryAction {

    private final FinanceScenarioRepository scenarios;
    private final PhaseFinanceRepository phases;
    private final CustomCostRepository customCosts;
    private final VendorCostRepository vendorCosts;
    private final FinanceSummaryRepository summaries;
    private final FinanceActivityLogger activityLogger;

    public RecalculateFinanceSummaryAction(FinanceScenarioRepository scenarios,
                                           PhaseFinanceRepository phases,
                                           CustomCostRepository customCosts,
                                           VendorCostRepository vendorCosts,
                                           FinanceSummaryRepository summaries,
                                           FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.phases = phases;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
        this.summaries = summaries;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID scenarioId, UUID projectId) {
        FinanceScenario scenario = scenarios.findById(scenarioId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        List<PhaseFinance> phaseList = phases.findAllByScenarioId(scenarioId);
        List<CustomCost> activeCosts = customCosts.findActiveByScenarioId(scenarioId);
        List<VendorCost> activeVendor = vendorCosts.findActiveByScenarioId(scenarioId);

        // Group costs by phase
        Map<UUID, BigDecimal> customCostByPhase = activeCosts.stream()
                .filter(c -> c.projectPhaseId() != null)
                .collect(Collectors.groupingBy(
                        CustomCost::projectPhaseId,
                        Collectors.reducing(BigDecimal.ZERO, CustomCost::amount, BigDecimal::add)));

        Map<UUID, BigDecimal> vendorCostByPhase = activeVendor.stream()
                .filter(v -> v.projectPhaseId() != null)
                .collect(Collectors.groupingBy(
                        VendorCost::projectPhaseId,
                        Collectors.reducing(BigDecimal.ZERO, VendorCost::amount, BigDecimal::add)));

        // Unassigned costs (no phase)
        BigDecimal unassignedCustom = activeCosts.stream()
                .filter(c -> c.projectPhaseId() == null)
                .map(CustomCost::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unassignedVendor = activeVendor.stream()
                .filter(v -> v.projectPhaseId() == null)
                .map(VendorCost::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDirectCostBase = BigDecimal.ZERO;
        BigDecimal totalEstimateHours = BigDecimal.ZERO;
        BigDecimal totalLaborCost = BigDecimal.ZERO;

        for (PhaseFinance phase : phaseList) {
            totalEstimateHours = totalEstimateHours.add(phase.estimateHours());
            totalLaborCost = totalLaborCost.add(phase.laborCost());
        }

        // Phase-level direct costs (before contingency/overhead)
        for (PhaseFinance phase : phaseList) {
            BigDecimal phaseCust = customCostByPhase.getOrDefault(phase.projectPhaseId(), BigDecimal.ZERO);
            BigDecimal phaseVend = vendorCostByPhase.getOrDefault(phase.projectPhaseId(), BigDecimal.ZERO);
            BigDecimal phaseDirectCost = phase.laborCost().add(phaseCust).add(phaseVend);
            totalDirectCostBase = totalDirectCostBase.add(phaseDirectCost);
        }
        // Add unassigned
        totalDirectCostBase = totalDirectCostBase.add(unassignedCustom).add(unassignedVendor);

        BigDecimal totalCustomCostAll = activeCosts.stream()
                .map(CustomCost::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVendorCostAll = activeVendor.stream()
                .map(VendorCost::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Contingency
        BigDecimal totalContingency = computeAdjustment(
                scenario.contingencyMethod(),
                totalDirectCostBase,
                scenario.contingencyPercent(),
                scenario.contingencyFixedAmount());

        // Overhead
        BigDecimal totalOverhead = computeAdjustment(
                scenario.overheadMethod(),
                totalDirectCostBase,
                scenario.overheadPercent(),
                scenario.overheadFixedAmount());

        BigDecimal budgetOfCosts = totalDirectCostBase.add(totalContingency).add(totalOverhead);
        BigDecimal plannedRevenue = scenario.plannedRevenue() != null ? scenario.plannedRevenue() : BigDecimal.ZERO;
        BigDecimal grossMargin = plannedRevenue.subtract(budgetOfCosts);
        BigDecimal grossMarginPercent = plannedRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossMargin.divide(plannedRevenue, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        BigDecimal profitBeforeTax = grossMargin;
        BigDecimal pbtPercent = grossMarginPercent;
        BigDecimal averageCostRate = totalEstimateHours.compareTo(BigDecimal.ZERO) > 0
                ? totalLaborCost.divide(totalEstimateHours, 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Distribute revenue to phases
        List<PhaseFinance> updatedPhases = distributeRevenueToPhases(
                phaseList, scenario, plannedRevenue,
                totalDirectCostBase, totalContingency, totalOverhead,
                customCostByPhase, vendorCostByPhase);

        phases.saveAll(updatedPhases);

        // Update summary
        FinanceSummary summary = summaries.findByScenarioId(scenarioId)
                .orElse(FinanceSummary.create(scenarioId, projectId, scenario.currencyCode()));

        FinanceSummary updated = summary.recalculate(
                totalEstimateHours, totalLaborCost, totalCustomCostAll, totalVendorCostAll,
                totalContingency, totalDirectCostBase, totalOverhead, budgetOfCosts,
                plannedRevenue, grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent,
                averageCostRate);

        summaries.save(updated);

        activityLogger.log(FinanceEntityTypes.FINANCE_SUMMARY, scenarioId,
                FinanceActivityActions.RECALCULATE, "Finance summary recalculated for scenario: " + scenarioId);
    }

    private BigDecimal computeAdjustment(CostAdjustmentMethod method, BigDecimal base,
                                          BigDecimal percent, BigDecimal fixedAmount) {
        if (method == null || method == CostAdjustmentMethod.NONE) {
            return BigDecimal.ZERO;
        }
        if (method == CostAdjustmentMethod.PERCENT && percent != null) {
            return base.multiply(percent).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }
        if (method == CostAdjustmentMethod.FIXED_AMOUNT && fixedAmount != null) {
            return fixedAmount;
        }
        return BigDecimal.ZERO;
    }

    private List<PhaseFinance> distributeRevenueToPhases(
            List<PhaseFinance> phaseList,
            FinanceScenario scenario,
            BigDecimal totalPlannedRevenue,
            BigDecimal totalDirectCost,
            BigDecimal totalContingency,
            BigDecimal totalOverhead,
            Map<UUID, BigDecimal> customCostByPhase,
            Map<UUID, BigDecimal> vendorCostByPhase) {

        int n = phaseList.size();
        if (n == 0) return phaseList;

        return phaseList.stream().map(phase -> {
            BigDecimal phaseCust = customCostByPhase.getOrDefault(phase.projectPhaseId(), BigDecimal.ZERO);
            BigDecimal phaseVend = vendorCostByPhase.getOrDefault(phase.projectPhaseId(), BigDecimal.ZERO);
            BigDecimal phaseDirectCost = phase.laborCost().add(phaseCust).add(phaseVend);

            BigDecimal phaseRevenue;
            BigDecimal phaseRevenuePercent;

            RevenueSplitMethod splitMethod = scenario.revenueSplitMethod();
            if (splitMethod == RevenueSplitMethod.EQUAL_SPLIT) {
                phaseRevenue = totalPlannedRevenue.divide(BigDecimal.valueOf(n), 6, RoundingMode.HALF_UP);
                phaseRevenuePercent = n > 0
                        ? BigDecimal.valueOf(100).divide(BigDecimal.valueOf(n), 6, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
            } else if (splitMethod == RevenueSplitMethod.EFFORT_BASED) {
                BigDecimal weight = totalDirectCost.compareTo(BigDecimal.ZERO) > 0
                        ? phaseDirectCost.divide(totalDirectCost, 6, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                phaseRevenue = totalPlannedRevenue.multiply(weight).setScale(6, RoundingMode.HALF_UP);
                phaseRevenuePercent = weight.multiply(BigDecimal.valueOf(100)).setScale(6, RoundingMode.HALF_UP);
            } else {
                // MANUAL or null — keep existing values
                phaseRevenue = phase.plannedRevenue();
                phaseRevenuePercent = phase.revenuePercent();
            }

            BigDecimal phaseContingency = totalDirectCost.compareTo(BigDecimal.ZERO) > 0
                    ? totalContingency.multiply(
                    phaseDirectCost.divide(totalDirectCost, 6, RoundingMode.HALF_UP))
                    : BigDecimal.ZERO;
            BigDecimal phaseOverhead = totalDirectCost.compareTo(BigDecimal.ZERO) > 0
                    ? totalOverhead.multiply(
                    phaseDirectCost.divide(totalDirectCost, 6, RoundingMode.HALF_UP))
                    : BigDecimal.ZERO;
            BigDecimal phaseBudgetOfCosts = phaseDirectCost.add(phaseContingency).add(phaseOverhead);
            BigDecimal phaseGrossMargin = phaseRevenue.subtract(phaseBudgetOfCosts);
            BigDecimal phaseGrossMarginPercent = phaseRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? phaseGrossMargin.divide(phaseRevenue, 6, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            return phase.withCalculatedValues(
                    phaseCust, phaseVend, phaseContingency, phaseDirectCost,
                    phaseOverhead, phaseBudgetOfCosts, phaseRevenue, phaseRevenuePercent,
                    phaseGrossMargin, phaseGrossMarginPercent, phaseGrossMargin, phaseGrossMarginPercent);
        }).toList();
    }
}
