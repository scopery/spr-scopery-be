package com.company.scopery.modules.projectfinance.vendorcost.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceActivityActions;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceEntityTypes;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import com.company.scopery.modules.projectfinance.vendorcost.application.response.VendorCostResponse;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveVendorCostAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectVendorCostRepository vendorCosts;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public ArchiveVendorCostAction(ProjectRepository projects,
                                   ProjectFinanceScenarioRepository scenarios,
                                   ProjectVendorCostRepository vendorCosts,
                                   FinanceScenarioRecalculator recalculator,
                                   ProjectFinanceAuthorizationService authorization,
                                   CurrentUserAuthorizationService currentUser,
                                   ProjectFinancePlatformPublisher publisher,
                                   ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.vendorCosts = vendorCosts;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public VendorCostResponse execute(UUID projectId, UUID scenarioId, UUID costId) {
        authorization.requireCostArchive(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(scenarioId));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        ProjectVendorCost existing = vendorCosts.findByIdAndScenarioId(costId, scenarioId)
                .orElseThrow(() -> ProjectFinanceExceptions.vendorCostNotFound(costId));
        UUID actorId = currentUser.resolveCurrentUser().id();
        ProjectVendorCost cost = vendorCosts.save(existing.archive(actorId));
        recalculator.recalculate(scenario, false);
        publisher.enqueueScenario(scenario, "PROJECT_VENDOR_COST_ARCHIVED");
        publisher.auditCostChanged(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.VENDOR_COST, cost.id(),
                ProjectFinanceActivityActions.PROJECT_VENDOR_COST_ARCHIVED, "Vendor cost archived");
        return VendorCostResponse.from(cost);
    }
}
