package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
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
public class RecalculateFinanceScenarioAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;
    private final ApplicationEventPublisher events;

    public RecalculateFinanceScenarioAction(ProjectRepository projects,
                                            ProjectFinanceScenarioRepository scenarios,
                                            ProjectFinanceAuthorizationService authorization,
                                            CurrentUserAuthorizationService currentUser,
                                            FinanceScenarioRecalculator recalculator,
                                            ProjectFinancePlatformPublisher publisher,
                                            ProjectFinanceActivityLogger activityLogger,
                                            ApplicationEventPublisher events) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.recalculator = recalculator;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.events = events;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        authorization.requireRecalculate(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        recalculator.recalculate(scenario, false);
        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_RECALCULATED");
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SUMMARY_CALCULATED");
        publisher.auditRecalculated(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_RECALCULATED,
                "Finance scenario recalculated");
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "FINANCE_SCENARIO_RECALCULATED"));
        return FinanceScenarioResponse.from(scenario);
    }
}
