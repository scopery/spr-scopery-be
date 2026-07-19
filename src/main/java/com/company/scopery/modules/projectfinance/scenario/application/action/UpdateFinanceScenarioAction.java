package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.scenario.application.command.UpdateFinanceScenarioCommand;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Component
public class UpdateFinanceScenarioAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final FinanceScenarioRecalculator recalculator;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public UpdateFinanceScenarioAction(ProjectRepository projects,
                                       ProjectFinanceScenarioRepository scenarios,
                                       ProjectFinanceAuthorizationService authorization,
                                       CurrentUserAuthorizationService currentUser,
                                       FinanceScenarioRecalculator recalculator,
                                       ProjectFinancePlatformPublisher publisher,
                                       ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.recalculator = recalculator;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UpdateFinanceScenarioCommand command) {
        authorization.requireUpdate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        ProjectFinanceScenario scenario = scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> ProjectFinanceExceptions.scenarioNotFound(command.scenarioId()));
        if (!scenario.isEditable()) {
            throw ProjectFinanceExceptions.notDraft(scenario.id());
        }

        RevenueSplitMethod revenueSplit = parseRevenue(command.revenueSplitMethod(), scenario.revenueSplitMethod());
        ContingencyMethod contingency = parseContingency(command.contingencyMethod(), scenario.contingencyMethod());
        OverheadMethod overhead = parseOverhead(command.overheadMethod(), scenario.overheadMethod());
        BigDecimal plannedRevenue = command.plannedRevenue() == null ? scenario.plannedRevenue() : command.plannedRevenue();
        if (plannedRevenue.signum() < 0) {
            throw ProjectFinanceExceptions.validationFailed("Planned revenue must be >= 0");
        }

        boolean contingencyChanged = !Objects.equals(contingency, scenario.contingencyMethod())
                || !Objects.equals(command.contingencyPercent(), scenario.contingencyPercent())
                || !Objects.equals(command.contingencyFixedAmount(), scenario.contingencyFixedAmount());
        boolean overheadChanged = !Objects.equals(overhead, scenario.overheadMethod())
                || !Objects.equals(command.overheadPercent(), scenario.overheadPercent())
                || !Objects.equals(command.overheadFixedAmount(), scenario.overheadFixedAmount());
        boolean revenueChanged = plannedRevenue.compareTo(scenario.plannedRevenue()) != 0
                || revenueSplit != scenario.revenueSplitMethod();

        scenario = scenarios.save(scenario.updateDraft(
                command.name() == null ? scenario.name() : command.name(),
                command.description() == null ? scenario.description() : command.description(),
                plannedRevenue, revenueSplit, contingency,
                command.contingencyPercent() == null ? scenario.contingencyPercent() : command.contingencyPercent(),
                command.contingencyFixedAmount() == null ? scenario.contingencyFixedAmount() : command.contingencyFixedAmount(),
                overhead,
                command.overheadPercent() == null ? scenario.overheadPercent() : command.overheadPercent(),
                command.overheadFixedAmount() == null ? scenario.overheadFixedAmount() : command.overheadFixedAmount(),
                command.targetMarginPercent() == null ? scenario.targetMarginPercent() : command.targetMarginPercent(),
                command.assumptionsJson() == null ? scenario.assumptionsJson() : command.assumptionsJson()));
        recalculator.recalculate(scenario, false);

        UUID actorId = currentUser.resolveCurrentUser().id();
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_UPDATED");
        if (revenueChanged) {
            publisher.enqueueScenario(scenario, "PROJECT_REVENUE_SPLIT_UPDATED");
            publisher.auditRevenueChanged(actorId, project, scenario);
            activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                    ProjectFinanceActivityActions.PROJECT_REVENUE_SPLIT_UPDATED, "Revenue settings updated");
        }
        if (overheadChanged) {
            publisher.enqueueScenario(scenario, "PROJECT_OVERHEAD_POLICY_UPDATED");
            publisher.auditOverheadChanged(actorId, project, scenario);
            activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                    ProjectFinanceActivityActions.PROJECT_OVERHEAD_POLICY_UPDATED, "Overhead policy updated");
        }
        if (contingencyChanged) {
            publisher.enqueueScenario(scenario, "PROJECT_CONTINGENCY_UPDATED");
            publisher.auditContingencyChanged(actorId, project, scenario);
            activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                    ProjectFinanceActivityActions.PROJECT_CONTINGENCY_UPDATED, "Contingency updated");
        }
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_UPDATED, "Finance scenario updated");
        return FinanceScenarioResponse.from(scenario);
    }

    private RevenueSplitMethod parseRevenue(String value, RevenueSplitMethod fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return ProjectFinanceEnumParser.parseRequired(RevenueSplitMethod.class, value,
                    ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_METHOD.code(), "revenueSplitMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidRevenueSplitMethod(value);
        }
    }

    private ContingencyMethod parseContingency(String value, ContingencyMethod fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return ProjectFinanceEnumParser.parseRequired(ContingencyMethod.class, value,
                    ProjectFinanceErrorCatalog.PROJECT_CONTINGENCY_INVALID.code(), "contingencyMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidContingency();
        }
    }

    private OverheadMethod parseOverhead(String value, OverheadMethod fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return ProjectFinanceEnumParser.parseRequired(OverheadMethod.class, value,
                    ProjectFinanceErrorCatalog.PROJECT_OVERHEAD_POLICY_INVALID.code(), "overheadMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidOverhead();
        }
    }
}
