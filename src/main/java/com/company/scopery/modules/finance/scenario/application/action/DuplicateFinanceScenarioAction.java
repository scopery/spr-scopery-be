package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
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

import java.util.List;
import java.util.UUID;

@Component
public class DuplicateFinanceScenarioAction {

    private final FinanceScenarioRepository scenarios;
    private final FinanceSummaryRepository summaries;
    private final PhaseFinanceRepository phases;
    private final CustomCostRepository customCosts;
    private final VendorCostRepository vendorCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public DuplicateFinanceScenarioAction(FinanceScenarioRepository scenarios,
                                          FinanceSummaryRepository summaries,
                                          PhaseFinanceRepository phases,
                                          CustomCostRepository customCosts,
                                          VendorCostRepository vendorCosts,
                                          RecalculateFinanceSummaryAction recalculate,
                                          FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.summaries = summaries;
        this.phases = phases;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId, String newCode, String newName) {
        FinanceScenario source = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        if (scenarios.existsByProjectIdAndCode(projectId, newCode)) {
            throw FinanceExceptions.scenarioCodeExists(newCode);
        }

        // Create new scenario as DRAFT copy
        FinanceScenario copy = FinanceScenario.create(
                source.projectId(),
                source.workspaceId(),
                source.estimationRunId(),
                newCode,
                newName != null ? newName : source.name() + " (Copy)",
                source.description(),
                source.currencyCode(),
                source.plannedRevenue(),
                source.revenueSplitMethod(),
                source.contingencyMethod(),
                source.contingencyPercent(),
                source.contingencyFixedAmount(),
                source.overheadMethod(),
                source.overheadPercent(),
                source.overheadFixedAmount(),
                source.targetMarginPercent(),
                source.assumptionsJson());

        FinanceScenario saved = scenarios.save(copy);
        UUID newScenarioId = saved.id();

        // Copy summary as empty
        FinanceSummary newSummary = FinanceSummary.create(newScenarioId, projectId, source.currencyCode());
        summaries.save(newSummary);

        // Copy phases
        List<PhaseFinance> sourcePhases = phases.findAllByScenarioId(scenarioId);
        List<PhaseFinance> copiedPhases = sourcePhases.stream()
                .map(p -> PhaseFinance.create(
                        newScenarioId, p.projectPhaseId(), p.phaseNameSnapshot(),
                        p.phaseOrder(), p.estimateHours(), p.laborCost()))
                .toList();
        if (!copiedPhases.isEmpty()) {
            phases.saveAll(copiedPhases);
        }

        // Copy custom costs
        List<CustomCost> sourceCustom = customCosts.findActiveByScenarioId(scenarioId);
        for (CustomCost c : sourceCustom) {
            CustomCost newCost = CustomCost.create(
                    newScenarioId, c.projectPhaseId(), c.category(),
                    c.name(), c.description(), c.amount(), c.currencyCode(), c.costDate());
            customCosts.save(newCost);
        }

        // Copy vendor costs
        List<VendorCost> sourceVendor = vendorCosts.findActiveByScenarioId(scenarioId);
        for (VendorCost v : sourceVendor) {
            VendorCost newCost = VendorCost.create(
                    newScenarioId, v.projectPhaseId(), v.vendorName(),
                    v.description(), v.amount(), v.currencyCode());
            vendorCosts.save(newCost);
        }

        recalculate.execute(newScenarioId, projectId);

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, saved.id(),
                FinanceActivityActions.DUPLICATE_SCENARIO,
                "Finance scenario duplicated from: " + source.code() + " to: " + newCode);

        return FinanceScenarioResponse.from(scenarios.findById(newScenarioId).orElse(saved));
    }
}
