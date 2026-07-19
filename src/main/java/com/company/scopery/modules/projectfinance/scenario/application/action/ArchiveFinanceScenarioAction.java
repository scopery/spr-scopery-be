package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceActivityActions;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceEntityTypes;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveFinanceScenarioAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public ArchiveFinanceScenarioAction(ProjectRepository projects,
                                        ProjectFinanceScenarioRepository scenarios,
                                        ProjectFinanceAuthorizationService authorization,
                                        CurrentUserAuthorizationService currentUser,
                                        ProjectFinancePlatformPublisher publisher,
                                        ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        authorization.requireArchive(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        UUID actorId = currentUser.resolveCurrentUser().id();
        boolean wasCurrent = scenario.currentFlag();
        scenario = scenarios.save(scenario.archive(actorId));
        if (wasCurrent && scenario.id().equals(project.currentFinanceScenarioId())) {
            projects.save(project.withCurrentFinanceScenarioId(null));
        }
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_ARCHIVED");
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_ARCHIVED, "Finance scenario archived");
        return FinanceScenarioResponse.from(scenario);
    }
}
