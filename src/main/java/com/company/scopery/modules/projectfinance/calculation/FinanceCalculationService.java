package com.company.scopery.modules.projectfinance.calculation;

import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FinanceCalculationService {

    public static final int MONEY_SCALE = 4;
    public static final int PERCENT_SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public FinanceCalculationResult calculate(
            ProjectFinanceScenario scenario,
            List<ProjectPhase> phases,
            List<PhaseEstimateRollup> phaseRollups,
            List<ProjectCustomCost> customCosts,
            List<ProjectVendorCost> vendorCosts,
            List<ProjectPhaseFinance> existingPhaseFinance,
            boolean reimportLabor) {

        Map<UUID, PhaseEstimateRollup> rollupByPhase = new HashMap<>();
        for (PhaseEstimateRollup rollup : phaseRollups) {
            rollupByPhase.put(rollup.projectPhaseId(), rollup);
        }

        Map<UUID, ProjectPhaseFinance> existingByPhase = new HashMap<>();
        for (ProjectPhaseFinance row : existingPhaseFinance) {
            existingByPhase.put(row.projectPhaseId(), row);
        }

        List<MutablePhase> mutable = new ArrayList<>();
        for (ProjectPhase phase : phases) {
            BigDecimal hours = BigDecimal.ZERO;
            BigDecimal labor = BigDecimal.ZERO;
            BigDecimal revenuePercent = null;
            BigDecimal manualRevenue = null;

            if (!reimportLabor && existingByPhase.containsKey(phase.id())) {
                ProjectPhaseFinance existing = existingByPhase.get(phase.id());
                hours = nz(existing.estimateHours());
                labor = nz(existing.laborCost());
                revenuePercent = existing.revenuePercent();
                manualRevenue = existing.plannedRevenue();
            } else {
                PhaseEstimateRollup rollup = rollupByPhase.get(phase.id());
                if (rollup != null) {
                    hours = nz(rollup.totalEstimateHours());
                    labor = nz(rollup.totalLaborCost());
                }
                if (existingByPhase.containsKey(phase.id())) {
                    ProjectPhaseFinance existing = existingByPhase.get(phase.id());
                    revenuePercent = existing.revenuePercent();
                    if (scenario.revenueSplitMethod() == RevenueSplitMethod.MANUAL_AMOUNT) {
                        manualRevenue = existing.plannedRevenue();
                    }
                }
            }

            mutable.add(new MutablePhase(
                    phase.id(), phase.name(), phase.displayOrder(), hours, labor,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, revenuePercent, manualRevenue));
        }

        if (mutable.isEmpty()) {
            throw ProjectFinanceExceptions.validationFailed("Project has no phases for finance calculation");
        }

        Map<UUID, BigDecimal> customByPhase = sumByPhase(
                customCosts.stream()
                        .filter(ProjectCustomCost::isActive)
                        .map(c -> new CostLine(c.projectPhaseId(), c.amount()))
                        .toList(),
                mutable);
        Map<UUID, BigDecimal> vendorByPhase = sumByPhase(
                vendorCosts.stream()
                        .filter(ProjectVendorCost::isActive)
                        .map(c -> new CostLine(c.projectPhaseId(), c.amount()))
                        .toList(),
                mutable);

        for (MutablePhase phase : mutable) {
            phase.customCost = nz(customByPhase.get(phase.phaseId));
            phase.vendorCost = nz(vendorByPhase.get(phase.phaseId));
        }

        BigDecimal totalLabor = mutable.stream().map(p -> p.laborCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCustom = mutable.stream().map(p -> p.customCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVendor = mutable.stream().map(p -> p.vendorCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal beforeContingency = totalLabor.add(totalCustom).add(totalVendor);

        BigDecimal totalContingency = computeContingency(scenario, totalLabor, beforeContingency);
        allocateByLabor(mutable, totalContingency, (phase, amount) -> phase.contingency = amount);

        for (MutablePhase phase : mutable) {
            phase.directCost = phase.laborCost.add(phase.customCost).add(phase.vendorCost).add(phase.contingency)
                    .setScale(MONEY_SCALE, ROUNDING);
        }

        BigDecimal totalDirect = mutable.stream().map(p -> p.directCost).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONEY_SCALE, ROUNDING);
        BigDecimal plannedRevenue = nz(scenario.plannedRevenue()).setScale(MONEY_SCALE, ROUNDING);

        BigDecimal totalOverhead = computeOverhead(scenario, totalLabor, totalDirect, plannedRevenue);
        allocateByLabor(mutable, totalOverhead, (phase, amount) -> phase.overhead = amount);

        applyRevenueSplit(scenario, mutable, totalDirect, plannedRevenue);

        for (MutablePhase phase : mutable) {
            phase.budgetOfCosts = phase.directCost.add(phase.overhead).setScale(MONEY_SCALE, ROUNDING);
            phase.grossMargin = phase.plannedRevenue.subtract(phase.directCost).setScale(MONEY_SCALE, ROUNDING);
            phase.grossMarginPercent = percentOrNull(phase.grossMargin, phase.plannedRevenue);
            phase.profitBeforeTax = phase.plannedRevenue.subtract(phase.directCost).subtract(phase.overhead)
                    .setScale(MONEY_SCALE, ROUNDING);
            phase.pbtPercent = percentOrNull(phase.profitBeforeTax, phase.plannedRevenue);
        }

        List<ProjectPhaseFinance> phaseRows = new ArrayList<>();
        for (MutablePhase phase : mutable) {
            UUID rowId = existingByPhase.containsKey(phase.phaseId)
                    ? existingByPhase.get(phase.phaseId).id()
                    : UUID.randomUUID();
            phaseRows.add(new ProjectPhaseFinance(
                    rowId, scenario.id(), scenario.projectId(), phase.phaseId, phase.phaseName,
                    phase.phaseOrder, scenario.currencyCode(),
                    phase.estimateHours.setScale(2, ROUNDING),
                    phase.laborCost.setScale(MONEY_SCALE, ROUNDING),
                    phase.customCost.setScale(MONEY_SCALE, ROUNDING),
                    phase.vendorCost.setScale(MONEY_SCALE, ROUNDING),
                    phase.contingency.setScale(MONEY_SCALE, ROUNDING),
                    phase.directCost,
                    phase.overhead.setScale(MONEY_SCALE, ROUNDING),
                    phase.budgetOfCosts,
                    phase.plannedRevenue.setScale(MONEY_SCALE, ROUNDING),
                    phase.revenuePercent,
                    phase.grossMargin,
                    phase.grossMarginPercent,
                    phase.profitBeforeTax,
                    phase.pbtPercent,
                    null, null));
        }

        BigDecimal totalHours = mutable.stream().map(p -> p.estimateHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal budgetOfCosts = totalDirect.add(totalOverhead).setScale(MONEY_SCALE, ROUNDING);
        BigDecimal grossMargin = plannedRevenue.subtract(totalDirect).setScale(MONEY_SCALE, ROUNDING);
        BigDecimal pbt = plannedRevenue.subtract(totalDirect).subtract(totalOverhead).setScale(MONEY_SCALE, ROUNDING);
        BigDecimal avgCostRate = totalHours.signum() == 0
                ? null
                : totalLabor.divide(totalHours, MONEY_SCALE, ROUNDING);

        ProjectFinanceSummary summary = ProjectFinanceSummary.create(
                scenario.id(),
                scenario.projectId(),
                scenario.currencyCode(),
                totalHours.setScale(2, ROUNDING),
                totalLabor.setScale(MONEY_SCALE, ROUNDING),
                totalCustom.setScale(MONEY_SCALE, ROUNDING),
                totalVendor.setScale(MONEY_SCALE, ROUNDING),
                totalContingency.setScale(MONEY_SCALE, ROUNDING),
                totalDirect,
                totalOverhead.setScale(MONEY_SCALE, ROUNDING),
                budgetOfCosts,
                plannedRevenue,
                grossMargin,
                percentOrNull(grossMargin, plannedRevenue),
                pbt,
                percentOrNull(pbt, plannedRevenue),
                avgCostRate,
                scenario.formulaVersion());

        return new FinanceCalculationResult(phaseRows, summary);
    }

    public void validateForApproval(ProjectFinanceScenario scenario, List<ProjectPhaseFinance> phases) {
        if (scenario.plannedRevenue() == null || scenario.plannedRevenue().signum() < 0) {
            throw ProjectFinanceExceptions.validationFailed("Planned revenue must be >= 0");
        }
        if (phases.isEmpty()) {
            throw ProjectFinanceExceptions.validationFailed("Scenario has no phase finance rows");
        }
        if (scenario.formulaVersion() == null || scenario.formulaVersion().isBlank()) {
            throw ProjectFinanceExceptions.validationFailed("Formula version is required");
        }

        if (scenario.revenueSplitMethod() == RevenueSplitMethod.MANUAL_PERCENT) {
            BigDecimal sum = phases.stream()
                    .map(p -> nz(p.revenuePercent()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sum.setScale(PERCENT_SCALE, ROUNDING).compareTo(HUNDRED.setScale(PERCENT_SCALE, ROUNDING)) != 0) {
                throw ProjectFinanceExceptions.revenuePercentInvalid();
            }
        }
        if (scenario.revenueSplitMethod() == RevenueSplitMethod.MANUAL_AMOUNT) {
            BigDecimal sum = phases.stream()
                    .map(p -> nz(p.plannedRevenue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(MONEY_SCALE, ROUNDING);
            if (sum.compareTo(nz(scenario.plannedRevenue()).setScale(MONEY_SCALE, ROUNDING)) != 0) {
                throw ProjectFinanceExceptions.revenueAmountInvalid();
            }
        }
        if (scenario.revenueSplitMethod() == RevenueSplitMethod.COST_PROPORTION) {
            BigDecimal totalDirect = phases.stream()
                    .map(p -> nz(p.directCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalDirect.signum() == 0 && nz(scenario.plannedRevenue()).signum() > 0) {
                throw ProjectFinanceExceptions.revenueDirectCostZero();
            }
        }
    }

    private BigDecimal computeContingency(ProjectFinanceScenario scenario,
                                          BigDecimal totalLabor,
                                          BigDecimal beforeContingency) {
        ContingencyMethod method = scenario.contingencyMethod();
        if (method == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING);
        }
        return switch (method) {
            case FIXED_AMOUNT -> {
                BigDecimal amount = nz(scenario.contingencyFixedAmount());
                if (amount.signum() < 0) {
                    throw ProjectFinanceExceptions.invalidContingency();
                }
                yield amount.setScale(MONEY_SCALE, ROUNDING);
            }
            case PERCENT_OF_LABOR_COST -> percentOf(totalLabor, scenario.contingencyPercent(), true);
            case PERCENT_OF_DIRECT_COST -> percentOf(beforeContingency, scenario.contingencyPercent(), true);
        };
    }

    private BigDecimal computeOverhead(ProjectFinanceScenario scenario,
                                       BigDecimal totalLabor,
                                       BigDecimal totalDirect,
                                       BigDecimal plannedRevenue) {
        OverheadMethod method = scenario.overheadMethod();
        if (method == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING);
        }
        return switch (method) {
            case FIXED_AMOUNT, PER_PHASE_FIXED, FTE_MONTH -> {
                BigDecimal amount = nz(scenario.overheadFixedAmount());
                if (amount.signum() < 0) {
                    throw ProjectFinanceExceptions.invalidOverhead();
                }
                yield amount.setScale(MONEY_SCALE, ROUNDING);
            }
            case PERCENT_OF_LABOR_COST -> percentOf(totalLabor, scenario.overheadPercent(), false);
            case PERCENT_OF_DIRECT_COST -> percentOf(totalDirect, scenario.overheadPercent(), false);
            case PERCENT_OF_REVENUE -> percentOf(plannedRevenue, scenario.overheadPercent(), false);
        };
    }

    private BigDecimal percentOf(BigDecimal base, BigDecimal percent, boolean contingency) {
        BigDecimal p = nz(percent);
        if (p.signum() < 0) {
            throw contingency
                    ? ProjectFinanceExceptions.invalidContingency()
                    : ProjectFinanceExceptions.invalidOverhead();
        }
        return base.multiply(p).divide(HUNDRED, MONEY_SCALE, ROUNDING);
    }

    private void applyRevenueSplit(ProjectFinanceScenario scenario,
                                   List<MutablePhase> phases,
                                   BigDecimal totalDirect,
                                   BigDecimal plannedRevenue) {
        RevenueSplitMethod method = scenario.revenueSplitMethod();
        switch (method) {
            case COST_PROPORTION -> {
                if (totalDirect.signum() == 0) {
                    if (plannedRevenue.signum() > 0) {
                        throw ProjectFinanceExceptions.revenueDirectCostZero();
                    }
                    for (MutablePhase phase : phases) {
                        phase.plannedRevenue = BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING);
                        phase.revenuePercent = BigDecimal.ZERO.setScale(PERCENT_SCALE, ROUNDING);
                    }
                    return;
                }
                BigDecimal allocated = BigDecimal.ZERO;
                for (int i = 0; i < phases.size(); i++) {
                    MutablePhase phase = phases.get(i);
                    if (i == phases.size() - 1) {
                        phase.plannedRevenue = plannedRevenue.subtract(allocated).setScale(MONEY_SCALE, ROUNDING);
                    } else {
                        phase.plannedRevenue = plannedRevenue.multiply(phase.directCost)
                                .divide(totalDirect, MONEY_SCALE, ROUNDING);
                        allocated = allocated.add(phase.plannedRevenue);
                    }
                    phase.revenuePercent = plannedRevenue.signum() == 0
                            ? BigDecimal.ZERO.setScale(PERCENT_SCALE, ROUNDING)
                            : phase.plannedRevenue.multiply(HUNDRED)
                            .divide(plannedRevenue, PERCENT_SCALE, ROUNDING);
                }
            }
            case MANUAL_PERCENT -> {
                BigDecimal sumPercent = phases.stream()
                        .map(p -> nz(p.revenuePercent))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sumPercent.setScale(PERCENT_SCALE, ROUNDING)
                        .compareTo(HUNDRED.setScale(PERCENT_SCALE, ROUNDING)) != 0) {
                    throw ProjectFinanceExceptions.revenuePercentInvalid();
                }
                BigDecimal allocated = BigDecimal.ZERO;
                for (int i = 0; i < phases.size(); i++) {
                    MutablePhase phase = phases.get(i);
                    BigDecimal pct = nz(phase.revenuePercent);
                    if (i == phases.size() - 1) {
                        phase.plannedRevenue = plannedRevenue.subtract(allocated).setScale(MONEY_SCALE, ROUNDING);
                    } else {
                        phase.plannedRevenue = plannedRevenue.multiply(pct)
                                .divide(HUNDRED, MONEY_SCALE, ROUNDING);
                        allocated = allocated.add(phase.plannedRevenue);
                    }
                    phase.revenuePercent = pct.setScale(PERCENT_SCALE, ROUNDING);
                }
            }
            case MANUAL_AMOUNT -> {
                BigDecimal sum = phases.stream()
                        .map(p -> nz(p.manualRevenue))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(MONEY_SCALE, ROUNDING);
                if (sum.compareTo(plannedRevenue) != 0) {
                    throw ProjectFinanceExceptions.revenueAmountInvalid();
                }
                for (MutablePhase phase : phases) {
                    phase.plannedRevenue = nz(phase.manualRevenue).setScale(MONEY_SCALE, ROUNDING);
                    phase.revenuePercent = plannedRevenue.signum() == 0
                            ? BigDecimal.ZERO.setScale(PERCENT_SCALE, ROUNDING)
                            : phase.plannedRevenue.multiply(HUNDRED)
                            .divide(plannedRevenue, PERCENT_SCALE, ROUNDING);
                }
            }
        }
    }

    private Map<UUID, BigDecimal> sumByPhase(List<CostLine> lines, List<MutablePhase> phases) {
        Map<UUID, BigDecimal> byPhase = new HashMap<>();
        BigDecimal unassigned = BigDecimal.ZERO;
        for (CostLine line : lines) {
            if (line.phaseId == null) {
                unassigned = unassigned.add(nz(line.amount));
            } else {
                byPhase.merge(line.phaseId, nz(line.amount), BigDecimal::add);
            }
        }
        if (unassigned.signum() > 0) {
            allocateByLabor(phases, unassigned, (phase, amount) ->
                    byPhase.merge(phase.phaseId, amount, BigDecimal::add));
        }
        return byPhase;
    }

    private void allocateByLabor(List<MutablePhase> phases, BigDecimal total, Allocator allocator) {
        BigDecimal amount = nz(total).setScale(MONEY_SCALE, ROUNDING);
        if (amount.signum() == 0 || phases.isEmpty()) {
            for (MutablePhase phase : phases) {
                allocator.apply(phase, BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING));
            }
            return;
        }
        BigDecimal laborTotal = phases.stream().map(p -> p.laborCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < phases.size(); i++) {
            MutablePhase phase = phases.get(i);
            BigDecimal share;
            if (i == phases.size() - 1) {
                share = amount.subtract(allocated).setScale(MONEY_SCALE, ROUNDING);
            } else if (laborTotal.signum() == 0) {
                share = amount.divide(BigDecimal.valueOf(phases.size()), MONEY_SCALE, ROUNDING);
                allocated = allocated.add(share);
            } else {
                share = amount.multiply(phase.laborCost).divide(laborTotal, MONEY_SCALE, ROUNDING);
                allocated = allocated.add(share);
            }
            allocator.apply(phase, share);
        }
    }

    private static BigDecimal percentOrNull(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.signum() == 0) {
            return null;
        }
        return numerator.multiply(HUNDRED).divide(denominator, PERCENT_SCALE, ROUNDING);
    }

    private static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @FunctionalInterface
    private interface Allocator {
        void apply(MutablePhase phase, BigDecimal amount);
    }

    private record CostLine(UUID phaseId, BigDecimal amount) {}

    private static final class MutablePhase {
        private final UUID phaseId;
        private final String phaseName;
        private final Integer phaseOrder;
        private final BigDecimal estimateHours;
        private final BigDecimal laborCost;
        private BigDecimal customCost;
        private BigDecimal vendorCost;
        private BigDecimal contingency;
        private BigDecimal directCost;
        private BigDecimal overhead;
        private BigDecimal budgetOfCosts;
        private BigDecimal plannedRevenue;
        private BigDecimal revenuePercent;
        private final BigDecimal manualRevenue;
        private BigDecimal grossMargin;
        private BigDecimal grossMarginPercent;
        private BigDecimal profitBeforeTax;
        private BigDecimal pbtPercent;

        private MutablePhase(UUID phaseId, String phaseName, Integer phaseOrder,
                             BigDecimal estimateHours, BigDecimal laborCost,
                             BigDecimal customCost, BigDecimal vendorCost, BigDecimal contingency,
                             BigDecimal directCost, BigDecimal overhead, BigDecimal budgetOfCosts,
                             BigDecimal plannedRevenue, BigDecimal revenuePercent, BigDecimal manualRevenue) {
            this.phaseId = phaseId;
            this.phaseName = phaseName;
            this.phaseOrder = phaseOrder;
            this.estimateHours = estimateHours;
            this.laborCost = laborCost;
            this.customCost = customCost;
            this.vendorCost = vendorCost;
            this.contingency = contingency;
            this.directCost = directCost;
            this.overhead = overhead;
            this.budgetOfCosts = budgetOfCosts;
            this.plannedRevenue = plannedRevenue;
            this.revenuePercent = revenuePercent;
            this.manualRevenue = manualRevenue;
        }
    }

    public record FinanceCalculationResult(
            List<ProjectPhaseFinance> phaseRows,
            ProjectFinanceSummary summary
    ) {}
}
