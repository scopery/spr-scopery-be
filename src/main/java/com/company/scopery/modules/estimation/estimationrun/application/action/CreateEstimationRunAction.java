package com.company.scopery.modules.estimation.estimationrun.application.action;

import com.company.scopery.modules.estimation.calculation.EstimationEngineOptions;
import com.company.scopery.modules.estimation.calculation.EstimationEngineService;
import com.company.scopery.modules.estimation.estimationrun.application.command.CreateEstimationRunCommand;
import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.shared.activity.EstimationActivityLogger;
import com.company.scopery.modules.estimation.shared.authorization.EstimationAuthorizationService;
import com.company.scopery.modules.estimation.shared.constant.EstimationActivityActions;
import com.company.scopery.modules.estimation.shared.constant.EstimationEntityTypes;
import com.company.scopery.modules.estimation.shared.error.EstimationErrorCatalog;
import com.company.scopery.modules.estimation.shared.error.EstimationExceptions;
import com.company.scopery.modules.estimation.shared.support.EstimationPlatformPublisher;
import com.company.scopery.modules.estimation.shared.util.EstimationEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateEstimationRunAction {

    private final ProjectRepository projects;
    private final EstimationRunRepository runs;
    private final EstimationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final EstimationEngineService engine;
    private final EstimationPlatformPublisher publisher;
    private final EstimationActivityLogger activityLogger;

    public CreateEstimationRunAction(ProjectRepository projects,
                                     EstimationRunRepository runs,
                                     EstimationAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     EstimationEngineService engine,
                                     EstimationPlatformPublisher publisher,
                                     EstimationActivityLogger activityLogger) {
        this.projects = projects;
        this.runs = runs;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.engine = engine;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EstimationRunResponse execute(CreateEstimationRunCommand command) {
        authorization.requireCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw EstimationExceptions.projectArchived(project.id());
        }

        CalculationMode mode = parseMode(command.calculationMode());
        RateTargetDateStrategy strategy = parseStrategy(command.rateTargetDateStrategy());
        CurrencyPolicy currencyPolicy = parseCurrency(command.currencyPolicy());

        UUID actorId = currentUser.resolveCurrentUser().id();
        String assumptions = "{"
                + "\"includeCompletedTasks\":" + command.includeCompletedTasks() + ","
                + "\"includeCancelledTasks\":" + command.includeCancelledTasks() + ","
                + "\"includeArchivedTasks\":" + command.includeArchivedTasks() + ","
                + "\"useBillingPreview\":" + command.useBillingPreview() + ","
                + "\"markAsCurrent\":" + command.markAsCurrent()
                + "}";

        EstimationRun run = EstimationRun.create(
                project.id(), project.workspaceId(), command.scheduleRunId(),
                command.name(), command.description(),
                mode, strategy, currencyPolicy, assumptions, actorId, MDC.get("traceId"));
        run = runs.save(run);
        publisher.enqueueRun(run, "ESTIMATION_RUN_CREATED");
        publisher.enqueueRun(run, "ESTIMATION_RUN_STARTED");
        activityLogger.logSuccess(EstimationEntityTypes.ESTIMATION_RUN, run.id(),
                EstimationActivityActions.ESTIMATION_RUN_CREATED, "Estimation run created");

        EstimationEngineOptions options = new EstimationEngineOptions(
                command.includeCompletedTasks(),
                command.includeCancelledTasks(),
                command.includeArchivedTasks(),
                command.useBillingPreview(),
                command.markAsCurrent());
        EstimationRun result = engine.execute(run, options);

        boolean completed = result.status() == EstimationRunStatus.COMPLETED;
        publisher.enqueueRun(result, completed ? "ESTIMATION_RUN_COMPLETED" : "ESTIMATION_RUN_FAILED");
        if (completed) {
            publisher.auditRunCompleted(actorId, project, result);
            publisher.auditRateSnapshotUsed(actorId, project, result);
            if (command.markAsCurrent()) {
                publisher.enqueueRun(result, "ESTIMATION_RUN_MARKED_CURRENT");
                publisher.auditMarkedCurrent(actorId, project, result);
                activityLogger.logSuccess(EstimationEntityTypes.ESTIMATION_RUN, result.id(),
                        EstimationActivityActions.ESTIMATION_RUN_MARKED_CURRENT, "Estimation run marked current");
            }
        } else {
            publisher.auditRunFailed(actorId, project, result);
        }
        activityLogger.logSuccess(EstimationEntityTypes.ESTIMATION_RUN, result.id(),
                completed ? EstimationActivityActions.ESTIMATION_RUN_COMPLETED
                        : EstimationActivityActions.ESTIMATION_RUN_FAILED,
                "Estimation run " + result.status().name().toLowerCase());
        return EstimationRunResponse.from(result);
    }

    private CalculationMode parseMode(String value) {
        String raw = (value == null || value.isBlank()) ? CalculationMode.TASK_ESTIMATE_WITH_RATE.name() : value;
        try {
            return EstimationEnumParser.parseRequired(CalculationMode.class, raw,
                    EstimationErrorCatalog.ESTIMATION_RUN_INVALID_MODE.code(), "calculationMode");
        } catch (RuntimeException ex) {
            throw EstimationExceptions.invalidMode(value);
        }
    }

    private RateTargetDateStrategy parseStrategy(String value) {
        String raw = (value == null || value.isBlank())
                ? RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START.name() : value;
        try {
            return EstimationEnumParser.parseRequired(RateTargetDateStrategy.class, raw,
                    EstimationErrorCatalog.ESTIMATION_RUN_INVALID_RATE_DATE_STRATEGY.code(),
                    "rateTargetDateStrategy");
        } catch (RuntimeException ex) {
            throw EstimationExceptions.invalidRateDateStrategy(value);
        }
    }

    private CurrencyPolicy parseCurrency(String value) {
        String raw = (value == null || value.isBlank())
                ? CurrencyPolicy.SINGLE_CURRENCY_REQUIRED.name() : value;
        try {
            return EstimationEnumParser.parseRequired(CurrencyPolicy.class, raw,
                    EstimationErrorCatalog.ESTIMATION_RUN_INVALID_CURRENCY_POLICY.code(), "currencyPolicy");
        } catch (RuntimeException ex) {
            throw EstimationExceptions.invalidCurrencyPolicy(value);
        }
    }
}
