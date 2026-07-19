package com.company.scopery.modules.projectfinance.phasefinance.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.phasefinance.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
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

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class UpdatePhaseRevenueAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public UpdatePhaseRevenueAction(ProjectRepository projects,
                                    ProjectFinanceScenarioRepository scenarios,
                                    ProjectPhaseFinanceRepository phaseFinance,
                                    FinanceScenarioRecalculator recalculator,
                                    ProjectFinanceAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    ProjectFinancePlatformPublisher publisher,
                                    ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.phaseFinance = phaseFinance;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PhaseFinanceResponse execute(UUID projectId, UUID scenarioId, UUID phaseFinanceId,
                                        BigDecimal plannedRevenue, BigDecimal revenuePercent) {
        authorization.requireRevenueUpdate(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        ProjectPhaseFinance existing = phaseFinance.findByIdAndScenarioId(phaseFinanceId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.phaseFinanceNotFound(phaseFinanceId));
        ProjectPhaseFinance updated = phaseFinance.save(existing.withRevenue(plannedRevenue, revenuePercent));
        recalculator.recalculate(scenario, false);
        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_REVENUE_SPLIT_UPDATED");
        publisher.auditRevenueChanged(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.PHASE_FINANCE, updated.id(),
                ProjectFinanceActivityActions.PROJECT_REVENUE_SPLIT_UPDATED, "Phase revenue updated");
        return PhaseFinanceResponse.from(phaseFinance.findByIdAndScenarioId(phaseFinanceId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.phaseFinanceNotFound(phaseFinanceId)));
    }
}
