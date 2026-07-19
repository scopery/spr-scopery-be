package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.PrepareApplyCommand;
import com.company.scopery.modules.airecommendation.application.port.RecommendationApplyPreparationPort;
import com.company.scopery.modules.airecommendation.application.response.PrepareApplyResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationStalenessService;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.domain.policy.RecommendationLifecyclePolicy;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionItemRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.activity.AiRecommendationActivityLogger;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationActivityActions;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PrepareApplySuggestionAction {

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionItemRepository itemRepository;
    private final RecommendationStalenessService stalenessService;
    private final RecommendationApplyPreparationPort applyPreparationPort;
    private final AiRecommendationActivityLogger activityLogger;

    public PrepareApplySuggestionAction(AiSuggestionRepository suggestionRepository,
                                         AiSuggestionItemRepository itemRepository,
                                         RecommendationStalenessService stalenessService,
                                         RecommendationApplyPreparationPort applyPreparationPort,
                                         AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.itemRepository = itemRepository;
        this.stalenessService = stalenessService;
        this.applyPreparationPort = applyPreparationPort;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PrepareApplyResponse execute(PrepareApplyCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        AiSuggestion suggestion = suggestionRepository.findById(ref.uuid())
                .filter(s -> cmd.workspaceId().equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(cmd.suggestionRef()));

        if (cmd.expectedVersion() >= 0 && suggestion.version() != cmd.expectedVersion()) {
            throw AiRecommendationExceptions.suggestionVersionConflict(
                    cmd.suggestionRef(), cmd.expectedVersion(), suggestion.version());
        }

        if (suggestion.status() != SuggestionStatus.ACCEPTED) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "PREPARE_APPLY");
        }

        if (stalenessService.isStale(suggestion)) {
            throw AiRecommendationExceptions.suggestionStale(cmd.suggestionRef());
        }

        if (RecommendationLifecyclePolicy.isTerminal(suggestion.status())) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "PREPARE_APPLY");
        }

        List<AiSuggestionItem> items = itemRepository.findBySuggestionId(suggestion.id());

        if (stalenessService.anyItemStale(items)) {
            throw AiRecommendationExceptions.suggestionStale(cmd.suggestionRef());
        }

        List<AiSuggestionItem> selectedItems = filterSelectedItems(items, cmd.selectedItemIds());

        Map<String, String> targetVersionTokens = selectedItems.stream()
                .filter(i -> i.targetEntityId() != null && i.expectedTargetVersionToken() != null)
                .collect(Collectors.toMap(
                        i -> i.targetEntityType() + ":" + i.targetEntityId(),
                        AiSuggestionItem::expectedTargetVersionToken,
                        (a, b) -> a));

        RecommendationApplyPreparationPort.PrepareApplyRequest request =
                new RecommendationApplyPreparationPort.PrepareApplyRequest(
                        cmd.suggestionRef(), suggestion.version(), cmd.workspaceId(),
                        suggestion.projectId(), cmd.actorId(),
                        selectedItems.stream().map(AiSuggestionItem::id).toList(),
                        targetVersionTokens, cmd.idempotencyKey(),
                        cmd.originConversationId(), MDC.get("traceId"));

        RecommendationApplyPreparationPort.PrepareApplyResult result = applyPreparationPort.prepare(request);

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.PREPARE_APPLY, "Prepare-apply attempted");

        return new PrepareApplyResponse(
                result.available(), result.suggestionRef(), result.actionRequestId(),
                result.actionPlanId(), result.planStatus(), result.confirmationRequired(),
                result.expiresAt(), null);
    }

    private List<AiSuggestionItem> filterSelectedItems(List<AiSuggestionItem> all, List<UUID> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            return all;
        }
        Set<UUID> selectedSet = Set.copyOf(selectedIds);
        return all.stream().filter(i -> selectedSet.contains(i.id())).toList();
    }
}
