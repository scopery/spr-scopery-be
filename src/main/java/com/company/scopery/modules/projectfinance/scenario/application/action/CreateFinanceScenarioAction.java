package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.calculation.FinanceCalculationService;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.command.CreateFinanceScenarioCommand;
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
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateFinanceScenarioAction {

    private final ProjectRepository projects;
    private final EstimationRunRepository estimationRuns;
    private final ProjectEstimateSummaryRepository estimateSummaries;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectFinanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final FinanceScenarioRecalculator recalculator;
    private final FinanceCalculationService calculationService;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final ProjectFinancePlatformPublisher publisher;
    private final ProjectFinanceActivityLogger activityLogger;

    public CreateFinanceScenarioAction(ProjectRepository projects,
                                       EstimationRunRepository estimationRuns,
                                       ProjectEstimateSummaryRepository estimateSummaries,
                                       ProjectFinanceScenarioRepository scenarios,
                                       ProjectFinanceAuthorizationService authorization,
                                       CurrentUserAuthorizationService currentUser,
                                       FinanceScenarioRecalculator recalculator,
                                       FinanceCalculationService calculationService,
                                       ProjectPhaseFinanceRepository phaseFinance,
                                       ProjectFinancePlatformPublisher publisher,
                                       ProjectFinanceActivityLogger activityLogger) {
        this.projects = projects;
        this.estimationRuns = estimationRuns;
        this.estimateSummaries = estimateSummaries;
        this.scenarios = scenarios;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.recalculator = recalculator;
        this.calculationService = calculationService;
        this.phaseFinance = phaseFinance;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(CreateFinanceScenarioCommand command) {
        authorization.requireCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectFinanceExceptions.projectArchived(project.id());
        }

        EstimationRun run = estimationRuns.findById(command.estimationRunId())
                .orElseThrow(() -> ProjectFinanceExceptions.estimationNotFound(command.estimationRunId()));
        if (!run.projectId().equals(project.id())) {
            throw ProjectFinanceExceptions.estimationProjectMismatch(run.id(), project.id());
        }
        if (run.status() != EstimationRunStatus.COMPLETED) {
            throw ProjectFinanceExceptions.estimationNotCompleted(run.id());
        }

        ProjectEstimateSummary estimateSummary = estimateSummaries.findByEstimationRunId(run.id())
                .orElseThrow(() -> ProjectFinanceExceptions.estimationNotFound(run.id()));

        String currency = command.currencyCode() == null || command.currencyCode().isBlank()
                ? estimateSummary.currencyCode()
                : command.currencyCode().trim().toUpperCase();
        if (currency == null || currency.isBlank()) {
            throw ProjectFinanceExceptions.currencyMismatch("required", currency);
        }
        if (estimateSummary.currencyCode() != null
                && !estimateSummary.currencyCode().equalsIgnoreCase(currency)) {
            throw ProjectFinanceExceptions.currencyMismatch(estimateSummary.currencyCode(), currency);
        }

        RevenueSplitMethod revenueSplit = parseRevenue(command.revenueSplitMethod());
        ContingencyMethod contingency = parseContingency(command.contingencyMethod());
        OverheadMethod overhead = parseOverhead(command.overheadMethod());
        validateNonNegative(command.contingencyPercent(), command.contingencyFixedAmount(), true);
        validateNonNegative(command.overheadPercent(), command.overheadFixedAmount(), false);

        UUID actorId = currentUser.resolveCurrentUser().id();
        ProjectFinanceScenario scenario = ProjectFinanceScenario.create(
                project.id(), project.workspaceId(), run.id(), command.code(), command.name(),
                command.description(), currency,
                command.plannedRevenue() == null ? BigDecimal.ZERO : command.plannedRevenue(),
                revenueSplit, contingency, command.contingencyPercent(), command.contingencyFixedAmount(),
                overhead, command.overheadPercent(), command.overheadFixedAmount(),
                command.targetMarginPercent(), command.assumptionsJson(), actorId, MDC.get("traceId"));
        scenario = scenarios.save(scenario);
        recalculator.recalculate(scenario, true);

        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_CREATED");
        publisher.enqueueScenario(scenario, "PROJECT_PHASE_FINANCE_CALCULATED");
        publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SUMMARY_CALCULATED");
        activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_CREATED, "Finance scenario created");

        if (command.markAsCurrent()) {
            recalculator.recalculate(scenario, false);
            calculationService.validateForApproval(scenario, phaseFinance.findByScenarioId(scenario.id()));
            for (ProjectFinanceScenario current : scenarios.findCurrentFlagged(project.id())) {
                scenarios.save(current.withCurrentFlag(false));
            }
            scenario = scenarios.save(scenario.approve(actorId).withCurrentFlag(true));
            projects.save(project.withCurrentFinanceScenarioId(scenario.id()));
            publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_APPROVED");
            publisher.enqueueScenario(scenario, "PROJECT_FINANCE_SCENARIO_MARKED_CURRENT");
            publisher.auditApproved(actorId, project, scenario);
            publisher.auditMarkedCurrent(actorId, project, scenario);
            activityLogger.logSuccess(ProjectFinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                    ProjectFinanceActivityActions.PROJECT_FINANCE_SCENARIO_MARKED_CURRENT,
                    "Finance scenario marked current on create");
        }
        return FinanceScenarioResponse.from(scenario);
    }

    private RevenueSplitMethod parseRevenue(String value) {
        String raw = (value == null || value.isBlank()) ? RevenueSplitMethod.COST_PROPORTION.name() : value;
        try {
            return ProjectFinanceEnumParser.parseRequired(RevenueSplitMethod.class, raw,
                    ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_METHOD.code(),
                    "revenueSplitMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidRevenueSplitMethod(value);
        }
    }

    private ContingencyMethod parseContingency(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ProjectFinanceEnumParser.parseRequired(ContingencyMethod.class, value,
                    ProjectFinanceErrorCatalog.PROJECT_CONTINGENCY_INVALID.code(), "contingencyMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidContingency();
        }
    }

    private OverheadMethod parseOverhead(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ProjectFinanceEnumParser.parseRequired(OverheadMethod.class, value,
                    ProjectFinanceErrorCatalog.PROJECT_OVERHEAD_POLICY_INVALID.code(), "overheadMethod");
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.invalidOverhead();
        }
    }

    private void validateNonNegative(BigDecimal percent, BigDecimal fixed, boolean contingency) {
        if (percent != null && percent.signum() < 0) {
            throw contingency ? ProjectFinanceExceptions.invalidContingency() : ProjectFinanceExceptions.invalidOverhead();
        }
        if (fixed != null && fixed.signum() < 0) {
            throw contingency ? ProjectFinanceExceptions.invalidContingency() : ProjectFinanceExceptions.invalidOverhead();
        }
    }
}
