package com.company.scopery.modules.aiplanning.apply.application.service;

import com.company.scopery.modules.aiplanning.apply.domain.enums.ApplyMode;
import com.company.scopery.modules.aiplanning.applyresult.domain.enums.ApplyResultStatus;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResult;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResultRepository;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.changerequest.application.action.CreateChangeRequestAction;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AiPlanningSafeApplyService {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSuggestionItemRepository items;
    private final AiPlanningApplyResultRepository applyResults;
    private final CreateChangeRequestAction createChangeRequestAction;

    public AiPlanningSafeApplyService(ProjectRepository projects,
                                      ProjectBaselineRepository baselines,
                                      AiPlanningSuggestionRepository suggestions,
                                      AiPlanningSuggestionItemRepository items,
                                      AiPlanningApplyResultRepository applyResults,
                                      CreateChangeRequestAction createChangeRequestAction) {
        this.projects = projects; this.baselines = baselines; this.suggestions = suggestions;
        this.items = items; this.applyResults = applyResults;
        this.createChangeRequestAction = createChangeRequestAction;
    }

    @Transactional
    public ApplyOutcome applySuggestion(UUID projectId, UUID suggestionId, UUID actorId,
                                        ApplyMode mode, boolean requireChangeRequestIfBaselined) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        AiPlanningSuggestion suggestion = suggestions.findByIdAndProjectId(suggestionId, projectId)
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(suggestionId));
        if (suggestion.status() != SuggestionStatus.ACCEPTED && suggestion.status() != SuggestionStatus.PARTIALLY_ACCEPTED) {
            throw AiPlanningExceptions.applyNotAccepted(suggestionId);
        }

        boolean hasBaseline = baselines.findCurrentByProjectId(projectId).isPresent()
                || project.currentBaselineId() != null;
        List<AiPlanningSuggestionItem> accepted = items.findBySuggestionId(suggestionId).stream()
                .filter(i -> i.status() == SuggestionItemStatus.ACCEPTED)
                .toList();
        if (accepted.isEmpty()) {
            throw AiPlanningExceptions.applyNotAccepted(suggestionId);
        }

        if (hasBaseline && requireChangeRequestIfBaselined) {
            return createChangeRequestOnly(project, suggestion, accepted, actorId);
        }
        if (mode == ApplyMode.CREATE_CHANGE_REQUEST_ONLY) {
            return createChangeRequestOnly(project, suggestion, accepted, actorId);
        }

        int success = 0;
        int skipped = 0;
        List<AiPlanningApplyResult> results = new ArrayList<>();
        for (AiPlanningSuggestionItem item : accepted) {
            // Direct structural mutation adapters are intentionally conservative in Phase 21:
            // proposal payloads are recorded; WBS/TASK creates go through ChangeRequest when baselined.
            // Non-baselined: draft/recommend/text items are marked applied as recorded proposals;
            // CREATE WBS/TASK are recorded as SKIPPED pending explicit domain command mapping from payload.
            if (item.itemType() == SuggestionItemType.WBS_NODE || item.itemType() == SuggestionItemType.TASK
                    || item.itemType() == SuggestionItemType.TASK_ESTIMATE
                    || item.itemType() == SuggestionItemType.TASK_DEPENDENCY) {
                var skippedItem = items.save(item.markSkipped("{\"reason\":\"USE_DOMAIN_FORM_OR_CHANGE_REQUEST\"}"));
                var result = applyResults.save(AiPlanningApplyResult.create(
                        suggestion.id(), skippedItem.id(), projectId, ApplyResultStatus.SKIPPED,
                        "DOMAIN_FORM_REQUIRED", item.itemType().name(), null,
                        "{\"message\":\"Accepted item recorded; apply via domain action or ChangeRequest\"}",
                        null, null, actorId, MDC.get("traceId")));
                results.add(result);
                skipped++;
            } else {
                var applied = items.save(item.markApplied(actorId, "RECORD_PROPOSAL", item.proposedPayloadJson()));
                var result = applyResults.save(AiPlanningApplyResult.create(
                        suggestion.id(), applied.id(), projectId, ApplyResultStatus.SUCCESS,
                        "RECORD_PROPOSAL", item.itemType().name(), null, item.proposedPayloadJson(),
                        null, null, actorId, MDC.get("traceId")));
                results.add(result);
                success++;
            }
        }

        if (skipped > 0 && success > 0) {
            suggestions.save(suggestion.markPartiallyApplied(actorId));
        } else if (success > 0 && skipped == 0) {
            suggestions.save(suggestion.markApplied(actorId));
        } else {
            suggestions.save(suggestion.markPartiallyApplied(actorId));
        }
        return new ApplyOutcome(suggestion.id(), results, false, null);
    }

    private ApplyOutcome createChangeRequestOnly(Project project, AiPlanningSuggestion suggestion,
                                                 List<AiPlanningSuggestionItem> accepted, UUID actorId) {
        String code = "AI-CR-" + suggestion.id().toString().substring(0, 8).toUpperCase();
        var cr = createChangeRequestAction.execute(new CreateChangeRequestCommand(
                project.id(),
                code,
                "AI suggestion: " + suggestion.title(),
                suggestion.summary(),
                "SCOPE_CHANGE",
                "MEDIUM",
                project.currentBaselineId(),
                "Created from accepted AI planning suggestion " + suggestion.id()));
        List<AiPlanningApplyResult> results = new ArrayList<>();
        for (AiPlanningSuggestionItem item : accepted) {
            var skipped = items.save(item.markSkipped("{\"changeRequestId\":\"" + cr.id() + "\"}"));
            results.add(applyResults.save(AiPlanningApplyResult.create(
                    suggestion.id(), skipped.id(), project.id(), ApplyResultStatus.CHANGE_REQUEST_REQUIRED,
                    "CreateChangeRequestAction", "CHANGE_REQUEST", cr.id(),
                    "{\"changeRequestId\":\"" + cr.id() + "\",\"itemId\":\"" + item.id() + "\"}",
                    null, null, actorId, MDC.get("traceId"))));
        }
        suggestions.save(suggestion.markPartiallyApplied(actorId));
        return new ApplyOutcome(suggestion.id(), results, true, cr.id());
    }

    public record ApplyOutcome(UUID suggestionId, List<AiPlanningApplyResult> results,
                               boolean changeRequestCreated, UUID changeRequestId) {}
}
