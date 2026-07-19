package com.company.scopery.modules.projectfinance.vendorcost.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
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
import com.company.scopery.modules.projectfinance.vendorcost.application.command.UpdateVendorCostCommand;
import com.company.scopery.modules.projectfinance.vendorcost.application.response.VendorCostResponse;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateVendorCostAction {

    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectVendorCostRepository vendorCosts;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public UpdateVendorCostAction(ProjectRepository projects,
                                  ProjectPhaseRepository phases,
                                  ProjectFinanceScenarioRepository scenarios,
                                  ProjectVendorCostRepository vendorCosts,
                                  FinanceScenarioRecalculator recalculator,
                                  ProjectFinanceAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser,
                                  ProjectFinancePlatformPublisher publisher,
                                  ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.phases = phases;
        this.scenarios = scenarios;
        this.vendorCosts = vendorCosts;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public VendorCostResponse execute(UpdateVendorCostCommand command) {
        authorization.requireCostUpdate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(command.scenarioId()));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }
        ProjectVendorCost existing = vendorCosts.findByIdAndScenarioId(command.costId(), command.scenarioId())
                .orElseThrow(() -> ProjectFinanceExceptions.vendorCostNotFound(command.costId()));
        if (command.amount() == null || command.amount().signum() < 0) {
            throw ProjectFinanceExceptions.vendorCostInvalidAmount();
        }
        if (command.description() == null || command.description().isBlank()) {
            throw ProjectFinanceExceptions.validationFailed("Vendor cost description is required");
        }
        String currency = command.currencyCode() == null || command.currencyCode().isBlank()
                ? scenario.currencyCode() : command.currencyCode().trim().toUpperCase();
        if (!currency.equalsIgnoreCase(scenario.currencyCode())) {
            throw ProjectFinanceExceptions.vendorCostCurrencyMismatch();
        }
        if (command.projectPhaseId() != null) {
            phases.findById(command.projectPhaseId())
                    .filter(p -> p.projectId().equals(command.projectId()))
                    .orElseThrow(() -> ProjectFinanceExceptions.vendorCostPhaseMismatch(command.projectPhaseId()));
        }
        ProjectVendorCost cost = vendorCosts.save(existing.update(
                command.projectPhaseId(), command.vendorName(), command.description(),
                command.amount(), currency));
        recalculator.recalculate(scenario, false);
        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_VENDOR_COST_UPDATED");
        publisher.auditCostChanged(actorId, project, scenario);
        activityLogger.logSuccess(ProjectFinanceEntityTypes.VENDOR_COST, cost.id(),
                ProjectFinanceActivityActions.PROJECT_VENDOR_COST_UPDATED, "Vendor cost updated");
        return VendorCostResponse.from(cost);
    }
}
