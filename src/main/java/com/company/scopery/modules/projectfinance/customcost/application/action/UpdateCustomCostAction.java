package com.company.scopery.modules.projectfinance.customcost.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.customcost.application.command.UpdateCustomCostCommand;
import com.company.scopery.modules.projectfinance.customcost.application.response.CustomCostResponse;
import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostCategory;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCostRepository;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceActivityActions;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceEntityTypes;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceErrorCatalog;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import com.company.scopery.modules.projectfinance.shared.util.ProjectFinanceEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateCustomCostAction {

    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectCustomCostRepository customCosts;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public UpdateCustomCostAction(ProjectRepository projects,
                                  ProjectPhaseRepository phases,
                                  ProjectFinanceScenarioRepository scenarios,
                                  ProjectCustomCostRepository customCosts,
                                  FinanceScenarioRecalculator recalculator,
                                  ProjectFinanceAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser,
                                  ProjectFinancePlatformPublisher publisher,
                                  ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.phases = phases;
        this.scenarios = scenarios;
        this.customCosts = customCosts;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CustomCostResponse execute(UpdateCustomCostCommand command) {
        authorization.requireCostUpdate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(command.scenarioId()));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        ProjectCustomCost existing = customCosts.findByIdAndScenarioId(command.costId(), command.scenarioId())
                .orElseThrow(() -> ProjectFinanceExceptions.customCostNotFound(command.costId()));
        if (command.amount() == null || command.amount().signum() < 0) {
            throw ProjectFinanceExceptions.customCostInvalidAmount();
        }
        CustomCostCategory category;
        try {
            category = ProjectFinanceEnumParser.parseRequired(CustomCostCategory.class, command.category(),
                    ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_INVALID_CATEGORY.code(), "category");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.customCostInvalidCategory(command.category());
        }
        if (category == CustomCostCategory.OTHER
                && (command.description() == null || command.description().isBlank())) {
            throw ProjectFinanceExceptions.customCostOtherRequiresDescription();
        }
        String currency = command.currencyCode() == null || command.currencyCode().isBlank()
                ? scenario.currencyCode() : command.currencyCode().trim().toUpperCase();
        if (!currency.equalsIgnoreCase(scenario.currencyCode())) {
            throw ProjectFinanceExceptions.customCostCurrencyMismatch();
        }
        if (command.projectPhaseId() != null) {
            phases.findById(command.projectPhaseId())
                    .filter(p -> p.projectId().equals(command.projectId()))
                    .orElseThrow(() -> ProjectFinanceExceptions.customCostPhaseMismatch(command.projectPhaseId()));
        }
        ProjectCustomCost cost = customCosts.save(existing.update(
                command.projectPhaseId(), category, command.name(), command.description(),
                command.amount(), currency, command.costDate()));
        recalculator.recalculate(scenario, false);
        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_CUSTOM_COST_UPDATED");
        publisher.auditCostChanged(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.CUSTOM_COST, cost.id(),
                ProjectFinanceActivityActions.PROJECT_CUSTOM_COST_UPDATED, "Custom cost updated");
        return CustomCostResponse.from(cost);
    }
}
