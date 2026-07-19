package com.company.scopery.modules.projectfinance.scenario.application.service;

import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.customcost.application.response.CustomCostResponse;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCostRepository;
import com.company.scopery.modules.projectfinance.phasefinance.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioCompareResponse;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.summary.application.response.FinanceSummaryResponse;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.projectfinance.vendorcost.application.response.VendorCostResponse;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class FinanceScenarioQueryService {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final ProjectFinanceSummaryRepository summaries;
    private final ProjectCustomCostRepository customCosts;
    private final ProjectVendorCostRepository vendorCosts;
    private final ProjectFinanceAuthorizationService authorization;

    public FinanceScenarioQueryService(ProjectRepository projects,
                                       ProjectFinanceScenarioRepository scenarios,
                                       ProjectPhaseFinanceRepository phaseFinance,
                                       ProjectFinanceSummaryRepository summaries,
                                       ProjectCustomCostRepository customCosts,
                                       ProjectVendorCostRepository vendorCosts,
                                       ProjectFinanceAuthorizationService authorization) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.phaseFinance = phaseFinance;
        this.summaries = summaries;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<FinanceScenarioResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return scenarios.findByProjectId(projectId).stream().map(FinanceScenarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FinanceScenarioResponse get(UUID projectId, UUID scenarioId) {
        authorization.requireView(projectId);
        return FinanceScenarioResponse.from(requireScenario(projectId, scenarioId));
    }

    @Transactional(readOnly = true)
    public List<PhaseFinanceResponse> listPhases(UUID projectId, UUID scenarioId) {
        authorization.requireView(projectId);
        requireScenario(projectId, scenarioId);
        return phaseFinance.findByScenarioId(scenarioId).stream().map(PhaseFinanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PhaseFinanceResponse getPhase(UUID projectId, UUID scenarioId, UUID phaseFinanceId) {
        authorization.requireView(projectId);
        requireScenario(projectId, scenarioId);
        return PhaseFinanceResponse.from(phaseFinance.findByIdAndScenarioId(phaseFinanceId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.phaseFinanceNotFound(phaseFinanceId)));
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse getSummary(UUID projectId, UUID scenarioId) {
        authorization.requireView(projectId);
        authorization.requireMarginView(projectId);
        requireScenario(projectId, scenarioId);
        return FinanceSummaryResponse.from(summaries.findByScenarioId(scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId)));
    }

    @Transactional(readOnly = true)
    public List<CustomCostResponse> listCustomCosts(UUID projectId, UUID scenarioId) {
        authorization.requireCostView(projectId);
        requireScenario(projectId, scenarioId);
        return customCosts.findByScenarioId(scenarioId).stream().map(CustomCostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomCostResponse getCustomCost(UUID projectId, UUID scenarioId, UUID costId) {
        authorization.requireCostView(projectId);
        requireScenario(projectId, scenarioId);
        return CustomCostResponse.from(customCosts.findByIdAndScenarioId(costId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.customCostNotFound(costId)));
    }

    @Transactional(readOnly = true)
    public List<VendorCostResponse> listVendorCosts(UUID projectId, UUID scenarioId) {
        authorization.requireCostView(projectId);
        requireScenario(projectId, scenarioId);
        return vendorCosts.findByScenarioId(scenarioId).stream().map(VendorCostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VendorCostResponse getVendorCost(UUID projectId, UUID scenarioId, UUID costId) {
        authorization.requireCostView(projectId);
        requireScenario(projectId, scenarioId);
        return VendorCostResponse.from(vendorCosts.findByIdAndScenarioId(costId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.vendorCostNotFound(costId)));
    }

    @Transactional(readOnly = true)
    public FinanceScenarioResponse getCurrent(UUID projectId) {
        authorization.requireView(projectId);
        return FinanceScenarioResponse.from(requireCurrent(projectId));
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse getCurrentSummary(UUID projectId) {
        authorization.requireView(projectId);
        authorization.requireMarginView(projectId);
        ProjectFinanceScenario current = requireCurrent(projectId);
        return FinanceSummaryResponse.from(summaries.findByScenarioId(current.id())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(current.id())));
    }

    @Transactional(readOnly = true)
    public List<PhaseFinanceResponse> getCurrentPhases(UUID projectId) {
        authorization.requireView(projectId);
        ProjectFinanceScenario current = requireCurrent(projectId);
        return phaseFinance.findByScenarioId(current.id()).stream().map(PhaseFinanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FinanceScenarioCompareResponse compare(UUID projectId, UUID leftScenarioId, UUID rightScenarioId) {
        authorization.requireView(projectId);
        authorization.requireMarginView(projectId);
        ProjectFinanceScenario left = requireScenario(projectId, leftScenarioId);
        ProjectFinanceScenario right = requireScenario(projectId, rightScenarioId);
        FinanceSummaryResponse leftSummary = FinanceSummaryResponse.from(summaries.findByScenarioId(left.id())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(left.id())));
        FinanceSummaryResponse rightSummary = FinanceSummaryResponse.from(summaries.findByScenarioId(right.id())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(right.id())));
        return new FinanceScenarioCompareResponse(
                projectId,
                left.id(),
                right.id(),
                FinanceScenarioResponse.from(left),
                FinanceScenarioResponse.from(right),
                leftSummary,
                rightSummary,
                new FinanceScenarioCompareResponse.FinanceSummaryDeltaResponse(
                        delta(rightSummary.plannedRevenue(), leftSummary.plannedRevenue()),
                        delta(rightSummary.budgetOfCosts(), leftSummary.budgetOfCosts()),
                        delta(rightSummary.grossMargin(), leftSummary.grossMargin()),
                        delta(rightSummary.grossMarginPercent(), leftSummary.grossMarginPercent()),
                        delta(rightSummary.profitBeforeTax(), leftSummary.profitBeforeTax()),
                        delta(rightSummary.totalEstimateHours(), leftSummary.totalEstimateHours())));
    }

    private BigDecimal delta(BigDecimal right, BigDecimal left) {
        if (right == null && left == null) {
            return BigDecimal.ZERO;
        }
        if (right == null) {
            return left.negate();
        }
        if (left == null) {
            return right;
        }
        return right.subtract(left);
    }

    private ProjectFinanceScenario requireScenario(UUID projectId, UUID scenarioId) {
        return scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
    }

    private ProjectFinanceScenario requireCurrent(UUID projectId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.currentFinanceScenarioId() != null) {
            return scenarios.findByIdAndProjectId(project.currentFinanceScenarioId(), projectId)
                    .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(project.currentFinanceScenarioId()));
        }
        return scenarios.findCurrentByProjectId(projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(null));
    }
}
