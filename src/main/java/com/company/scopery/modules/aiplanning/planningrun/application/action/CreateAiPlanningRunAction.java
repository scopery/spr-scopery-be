package com.company.scopery.modules.aiplanning.planningrun.application.action;

import com.company.scopery.modules.aiplanning.context.application.service.AiPlanningContextBuilder;
import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshot;
import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshotRepository;
import com.company.scopery.modules.aiplanning.planningrun.application.command.CreateAiPlanningRunCommand;
import com.company.scopery.modules.aiplanning.planningrun.application.response.AiPlanningRunResponse;
import com.company.scopery.modules.aiplanning.planningrun.application.service.AiPlanningSuggestionGenerator;
import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRunRepository;
import com.company.scopery.modules.aiplanning.shared.activity.AiPlanningActivityLogger;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningActivityActions;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningEntityTypes;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.shared.support.AiPlanningPlatformPublisher;
import com.company.scopery.modules.aiplanning.shared.util.AiPlanningEnumParser;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateAiPlanningRunAction {
    private final ProjectRepository projects;
    private final AiPlanningRunRepository runs;
    private final AiPlanningContextSnapshotRepository snapshots;
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSuggestionItemRepository items;
    private final AiPlanningContextBuilder contextBuilder;
    private final AiPlanningSuggestionGenerator generator;
    private final AiPlanningAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final AiPlanningPlatformPublisher publisher;
    private final AiPlanningActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CreateAiPlanningRunAction(ProjectRepository projects, AiPlanningRunRepository runs,
                                     AiPlanningContextSnapshotRepository snapshots,
                                     AiPlanningSuggestionRepository suggestions,
                                     AiPlanningSuggestionItemRepository items,
                                     AiPlanningContextBuilder contextBuilder,
                                     AiPlanningSuggestionGenerator generator,
                                     AiPlanningAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     AiPlanningPlatformPublisher publisher,
                                     AiPlanningActivityLogger activityLogger,
                                     ObjectMapper objectMapper) {
        this.projects = projects; this.runs = runs; this.snapshots = snapshots;
        this.suggestions = suggestions; this.items = items; this.contextBuilder = contextBuilder;
        this.generator = generator; this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger; this.objectMapper = objectMapper;
    }

    @Transactional
    public AiPlanningRunResponse execute(CreateAiPlanningRunCommand command) {
        authorization.requireRun(command.projectId());
        PlanningRunType runType = AiPlanningEnumParser.parseRequired(PlanningRunType.class, command.runType(), "runType");
        requireRunTypePermission(command.projectId(), runType);

        var actor = currentUser.resolveCurrentUser();
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw AiPlanningExceptions.projectArchived(project.id());
        }

        String inputJson = toJson(command.input());
        AiPlanningRun run = AiPlanningRun.create(
                project.id(), project.workspaceId(), actor.id(), command.agentId(),
                runType, inputJson, MDC.get("traceId"));
        run = runs.save(run);
        publisher.enqueueRun(run, "AI_PLANNING_RUN_CREATED");
        activityLogger.logSuccess(AiPlanningEntityTypes.PLANNING_RUN, run.id(),
                AiPlanningActivityActions.RUN_CREATED, "AI planning run created: " + runType);

        run = runs.save(run.markRunning());
        publisher.enqueueRun(run, "AI_PLANNING_RUN_STARTED");

        try {
            AiPlanningContextSnapshot snapshot = contextBuilder.build(
                    project.id(), actor.id(), command.includeSections(), MDC.get("traceId"));
            snapshot = snapshots.save(snapshot);
            publisher.enqueueRun(run, "AI_PLANNING_CONTEXT_SNAPSHOT_CREATED");

            boolean createSuggestions = command.options() == null
                    || !Boolean.FALSE.equals(command.options().get("createSuggestions"));
            String outputSummary = "{\"created\":false}";
            if (createSuggestions) {
                var generated = generator.generate(run.id(), project.id(), project.workspaceId(), runType, command.input());
                var suggestion = suggestions.save(generated.suggestion());
                for (var item : generated.items()) {
                    items.save(item);
                }
                publisher.enqueueSuggestion(suggestion, "AI_PLANNING_SUGGESTION_GENERATED");
                activityLogger.logSuccess(AiPlanningEntityTypes.SUGGESTION, suggestion.id(),
                        AiPlanningActivityActions.SUGGESTION_GENERATED, "Suggestion generated");
                outputSummary = generated.outputSummaryJson();
            }

            run = runs.save(run.markCompleted(snapshot.id(), outputSummary));
            publisher.enqueueRun(run, "AI_PLANNING_RUN_COMPLETED");
            activityLogger.logSuccess(AiPlanningEntityTypes.PLANNING_RUN, run.id(),
                    AiPlanningActivityActions.RUN_COMPLETED, "AI planning run completed");
            return AiPlanningRunResponse.from(run);
        } catch (RuntimeException ex) {
            run = runs.save(run.markFailed("AI_PLANNING_RUN_FAILED", ex.getMessage()));
            publisher.enqueueRun(run, "AI_PLANNING_RUN_FAILED");
            activityLogger.logSuccess(AiPlanningEntityTypes.PLANNING_RUN, run.id(),
                    AiPlanningActivityActions.RUN_FAILED, "AI planning run failed");
            throw ex;
        }
    }

    private void requireRunTypePermission(UUID projectId, PlanningRunType runType) {
        switch (runType) {
            case PROJECT_PLAN_DRAFT -> authorization.requirePlanDraft(projectId);
            case FINANCE_INSIGHT -> authorization.requireFinanceInsight(projectId);
            case QUOTE_PROPOSAL_DRAFT -> authorization.requireQuoteDraft(projectId);
            default -> { }
        }
    }

    private String toJson(Object value) {
        try { return objectMapper.writeValueAsString(value == null ? java.util.Map.of() : value); }
        catch (Exception e) { return "{}"; }
    }
}
