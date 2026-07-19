package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceCalculationService;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceActivityActions;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceEntityTypes;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkCurrentFinanceScenarioAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final ProjectEstimateSummaryRepository estimateSummaries;
    private final FinanceCalculationService calculationService;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;
    private final ApplicationEventPublisher events;

    public MarkCurrentFinanceScenarioAction(ProjectRepository projects,
                                            ProjectFinanceScenarioRepository scenarios,
                                            ProjectPhaseFinanceRepository phaseFinance,
                                            ProjectEstimateSummaryRepository estimateSummaries,
                                            FinanceCalculationService calculationService,
                                            ProjectFinanceAuthorizationService authorization,
                                            CurrentUserAuthorizationService currentUser,
                                            ProjectFinancePlatformPublisher publisher,
                                            ProjectFinanceActivityLogger activityLogger,
                                            ApplicationEventPublisher events) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.phaseFinance = phaseFinance;
        this.estimateSummaries = estimateSummaries;
        this.calculationService = calculationService;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.events = events;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        authorization.requireMarkCurrent(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        if (scenario.status() == FinanceScenarioStatus.ARCHIVED) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        if (scenario.status() == FinanceScenarioStatus.DRAFT) {
            UUID scenarioIdForValidation = scenario.id();
            estimateSummaries.findByEstimationRunId(scenario.estimationRunId()).ifPresent(summary -> {
                if (summary.unresolvedRateTaskCount() > 0) {
                    throw ProjectFinanceExceptions.unresolvedRates(scenarioIdForValidation);
                }
            });
            calculationService.validateForApproval(scenario, phaseFinance.findByScenarioId(scenario.id()));
            UUID approveActorId = currentUser.resolveCurrentUser().id();
            scenario = scenarios.save(scenario.approve(approveActorId));
            publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_APPROVED");
            publisher.auditApproved(approveActorId, project, scenario);
        }

        for (ProjectFinanceScenario current : scenarios.findCurrentFlagged(projectId)) {
            if (!current.id().equals(scenario.id())) {
                scenarios.save(current.withCurrentFlag(false));
            }
        }
        scenario = scenarios.save(scenario.withCurrentFlag(true));
        projects.save(project.withCurrentFinanceScenarioId(scenario.id()));

        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_MARKED_CURRENT");
        publisher.auditMarkedCurrent(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_MARKED_CURRENT,
                "Finance scenario marked current");
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "FINANCE_SCENARIO_MARKED_CURRENT"));
        return FinanceScenarioResponse.from(scenario);
    }
}
