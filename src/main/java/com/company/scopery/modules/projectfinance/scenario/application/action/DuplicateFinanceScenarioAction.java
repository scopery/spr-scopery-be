package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCostRepository;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceActivityActions;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceEntityTypes;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DuplicateFinanceScenarioAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final ProjectCustomCostRepository customCosts;
    private final ProjectVendorCostRepository vendorCosts;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public DuplicateFinanceScenarioAction(ProjectRepository projects,
                                          ProjectFinanceScenarioRepository scenarios,
                                          ProjectPhaseFinanceRepository phaseFinance,
                                          ProjectCustomCostRepository customCosts,
                                          ProjectVendorCostRepository vendorCosts,
                                          FinanceScenarioRecalculator recalculator,
                                          ProjectFinanceAuthorizationService authorization,
                                          CurrentUserAuthorizationService currentUser,
                                          ProjectFinancePlatformPublisher publisher,
                                          ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.phaseFinance = phaseFinance;
        this.customCosts = customCosts;
        this.vendorCosts = vendorCosts;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        authorization.requireCreate(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectFinanceExceptions.projectArchived(project.id());
        }
        ProjectFinanceScenario source = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        UUID actorId = currentUser.resolveCurrentUser().id();
        ProjectFinanceScenario copy = scenarios.save(source.asDraftDuplicate(
                source.name() + " (copy)", actorId, MDC.get("traceId")));

        for (ProjectPhaseFinance phase : phaseFinance.findByScenarioId(source.id())) {
            phaseFinance.save(new ProjectPhaseFinance(
                    UUID.randomUUID(), copy.id(), copy.projectId(), phase.projectPhaseId(),
                    phase.phaseNameSnapshot(), phase.phaseOrder(), phase.currencyCode(),
                    phase.estimateHours(), phase.laborCost(), phase.customCost(), phase.vendorCost(),
                    phase.contingencyAmount(), phase.directCost(), phase.overheadAmount(),
                    phase.budgetOfCosts(), phase.plannedRevenue(), phase.revenuePercent(),
                    phase.grossMargin(), phase.grossMarginPercent(), phase.profitBeforeTax(),
                    phase.pbtPercent(), null, null));
        }
        for (ProjectCustomCost cost : customCosts.findActiveByScenarioId(source.id())) {
            customCosts.save(ProjectCustomCost.create(
                    copy.id(), copy.projectId(), cost.projectPhaseId(), cost.category(),
                    cost.name(), cost.description(), cost.amount(), cost.currencyCode(), cost.costDate()));
        }
        for (ProjectVendorCost cost : vendorCosts.findActiveByScenarioId(source.id())) {
            vendorCosts.save(ProjectVendorCost.create(
                    copy.id(), copy.projectId(), cost.projectPhaseId(), cost.vendorName(),
                    cost.description(), cost.amount(), cost.currencyCode()));
        }
        recalculator.recalculate(copy, false);
        publisher.enqueueScenario(copy, "PROJECT_FINANCE_SCENARIO_DUPLICATED");
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, copy.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_DUPLICATED,
                "Finance scenario duplicated from " + source.id());
        return FinanceScenarioResponse.from(copy);
    }
}
