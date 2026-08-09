package com.company.scopery.modules.finance.scenario.application.service;

import com.company.scopery.modules.finance.scenario.application.response.CustomCostResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceCompareResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceSummaryResponse;
import com.company.scopery.modules.finance.scenario.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.finance.scenario.application.response.VendorCostResponse;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCostRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummary;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummaryRepository;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinanceRepository;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCostRepository;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FinanceQueryService {

    private final FinanceScenarioRepository scenarios;
    private final FinanceSummaryRepository summaries;
    private final PhaseFinanceRepository phases;
    private final CustomCostRepository customCosts;
    private final VendorCostRepository vendorCosts;

    public FinanceQueryService(FinanceScenarioRepository scenarios,
                               FinanceSummaryRepository summaries,
                               PhaseFinanceRepository phases,
                               CustomCostRepository customCosts,
                               VendorCostRepository vendorCosts) {
        this.scenarios = scenarios;
        this.summaries = summaries;
        this.phases = phases;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
    }

    @Transactional(readOnly = true)
    public FinanceScenarioResponse getScenario(UUID projectId, UUID scenarioId) {
        return scenarios.findByIdAndProjectId(scenarioId, projectId)
                .map(FinanceScenarioResponse::from)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));
    }

    @Transactional(readOnly = true)
    public List<FinanceScenarioResponse> listScenarios(UUID projectId) {
        return scenarios.findAllByProjectId(projectId)
                .stream().map(FinanceScenarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse getSummary(UUID projectId, UUID scenarioId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));
        return summaries.findByScenarioId(scenarioId)
                .map(FinanceSummaryResponse::from)
                .orElseThrow(() -> FinanceExceptions.summaryNotFound(scenarioId));
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse getCurrentSummary(UUID projectId) {
        Optional<FinanceScenario> current = scenarios.findCurrentByProjectId(projectId);
        if (current.isEmpty()) return null;
        return summaries.findByScenarioId(current.get().id())
                .map(FinanceSummaryResponse::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public FinanceScenarioResponse getCurrent(UUID projectId) {
        return scenarios.findCurrentByProjectId(projectId)
                .map(FinanceScenarioResponse::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PhaseFinanceResponse> getPhases(UUID projectId, UUID scenarioId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));
        return phases.findAllByScenarioId(scenarioId)
                .stream().map(PhaseFinanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PhaseFinanceResponse> getCurrentPhases(UUID projectId) {
        Optional<FinanceScenario> current = scenarios.findCurrentByProjectId(projectId);
        if (current.isEmpty()) return List.of();
        return phases.findAllByScenarioId(current.get().id())
                .stream().map(PhaseFinanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CustomCostResponse> getCustomCosts(UUID projectId, UUID scenarioId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));
        return customCosts.findAllByScenarioId(scenarioId)
                .stream().map(CustomCostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<VendorCostResponse> getVendorCosts(UUID projectId, UUID scenarioId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));
        return vendorCosts.findAllByScenarioId(scenarioId)
                .stream().map(VendorCostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FinanceCompareResponse compareScenarios(UUID projectId, UUID leftId, UUID rightId) {
        FinanceScenario left = scenarios.findByIdAndProjectId(leftId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(leftId));
        FinanceScenario right = scenarios.findByIdAndProjectId(rightId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(rightId));

        FinanceSummary leftSummary = summaries.findByScenarioId(leftId)
                .orElseThrow(() -> FinanceExceptions.summaryNotFound(leftId));
        FinanceSummary rightSummary = summaries.findByScenarioId(rightId)
                .orElseThrow(() -> FinanceExceptions.summaryNotFound(rightId));

        BigDecimal deltaTotalDirectCost = rightSummary.totalDirectCost().subtract(leftSummary.totalDirectCost());
        BigDecimal deltaBudgetOfCosts = rightSummary.budgetOfCosts().subtract(leftSummary.budgetOfCosts());
        BigDecimal deltaPlannedRevenue = rightSummary.plannedRevenue().subtract(leftSummary.plannedRevenue());
        BigDecimal deltaGrossMargin = rightSummary.grossMargin().subtract(leftSummary.grossMargin());
        BigDecimal deltaGrossMarginPercent = rightSummary.grossMarginPercent().subtract(leftSummary.grossMarginPercent());
        BigDecimal deltaProfitBeforeTax = rightSummary.profitBeforeTax().subtract(leftSummary.profitBeforeTax());
        BigDecimal deltaPbtPercent = rightSummary.pbtPercent().subtract(leftSummary.pbtPercent());

        return new FinanceCompareResponse(
                FinanceScenarioResponse.from(left),
                FinanceScenarioResponse.from(right),
                FinanceSummaryResponse.from(leftSummary),
                FinanceSummaryResponse.from(rightSummary),
                deltaTotalDirectCost,
                deltaBudgetOfCosts,
                deltaPlannedRevenue,
                deltaGrossMargin,
                deltaGrossMarginPercent,
                deltaProfitBeforeTax,
                deltaPbtPercent
        );
    }
}
