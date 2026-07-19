package com.company.scopery.modules.projectfinance.calculation;

import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCostRepository;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FinanceScenarioRecalculator {

    private final FinanceCalculationService calculationService;
    private final ProjectPhaseRepository phases;
    private final PhaseEstimateRollupRepository phaseRollups;
    private final ProjectCustomCostRepository customCosts;
    private final ProjectVendorCostRepository vendorCosts;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final ProjectFinanceSummaryRepository summaries;

    public FinanceScenarioRecalculator(FinanceCalculationService calculationService,
                                       ProjectPhaseRepository phases,
                                       PhaseEstimateRollupRepository phaseRollups,
                                       ProjectCustomCostRepository customCosts,
                                       ProjectVendorCostRepository vendorCosts,
                                       ProjectPhaseFinanceRepository phaseFinance,
                                       ProjectFinanceSummaryRepository summaries) {
        this.calculationService = calculationService;
        this.phases = phases;
        this.phaseRollups = phaseRollups;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
        this.phaseFinance = phaseFinance;
        this.summaries = summaries;
    }

    public FinanceCalculationService.FinanceCalculationResult recalculate(
            ProjectFinanceScenario scenario, boolean reimportLabor) {
        List<ProjectPhase> projectPhases = phases.findAllByProjectId(scenario.projectId());
        List<PhaseEstimateRollup> rollups = phaseRollups.findAllByEstimationRunId(scenario.estimationRunId());
        List<ProjectCustomCost> customs = customCosts.findActiveByScenarioId(scenario.id());
        List<ProjectVendorCost> vendors = vendorCosts.findActiveByScenarioId(scenario.id());
        List<ProjectPhaseFinance> existing = phaseFinance.findByScenarioId(scenario.id());

        FinanceCalculationService.FinanceCalculationResult result = calculationService.calculate(
                scenario, projectPhases, rollups, customs, vendors, existing, reimportLabor);

        phaseFinance.deleteByScenarioId(scenario.id());
        phaseFinance.saveAll(result.phaseRows());

        summaries.findByScenarioId(scenario.id()).ifPresent(s -> summaries.deleteByScenarioId(scenario.id()));
        ProjectFinanceSummary summary = result.summary();
        summaries.save(summary);
        return result;
    }
}
