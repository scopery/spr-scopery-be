package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.EditSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.response.EditSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationStalenessService;
import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.domain.policy.RecommendationLifecyclePolicy;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionItemRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionReviewRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.activity.AiRecommendationActivityLogger;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationActivityActions;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EditSuggestionAction {

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionItemRepository itemRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final RecommendationStalenessService stalenessService;
    private final AiRecommendationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public EditSuggestionAction(AiSuggestionRepository suggestionRepository,
                                 AiSuggestionItemRepository itemRepository,
                                 AiSuggestionReviewRepository reviewRepository,
                                 RecommendationStalenessService stalenessService,
                                 AiRecommendationActivityLogger activityLogger,
                                 ObjectMapper objectMapper) {
        this.suggestionRepository = suggestionRepository;
        this.itemRepository = itemRepository;
        this.reviewRepository = reviewRepository;
        this.stalenessService = stalenessService;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EditSuggestionResponse execute(EditSuggestionCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        if (ref.isPhase21()) {
            throw AiRecommendationExceptions.legacySuggestionEditUnavailable(cmd.suggestionRef());
        }

        AiSuggestion suggestion = suggestionRepository.findById(ref.uuid())
                .filter(s -> cmd.workspaceId().equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(cmd.suggestionRef()));

        if (cmd.expectedVersion() >= 0 && suggestion.version() != cmd.expectedVersion()) {
            throw AiRecommendationExceptions.suggestionVersionConflict(
                    cmd.suggestionRef(), cmd.expectedVersion(), suggestion.version());
        }

        if (stalenessService.isStale(suggestion)) {
            throw AiRecommendationExceptions.suggestionStale(cmd.suggestionRef());
        }

        if (!RecommendationLifecyclePolicy.canTransitionTo(suggestion.status(), SuggestionStatus.EDITED)) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "EDIT");
        }

        List<AiSuggestionItem> existingItems = itemRepository.findBySuggestionId(suggestion.id());
        Set<UUID> validItemIds = existingItems.stream().map(AiSuggestionItem::id).collect(Collectors.toSet());

        // Validate all edit targets are valid item IDs for this suggestion
        for (EditSuggestionCommand.EditItemCommand editItem : cmd.items()) {
            if (!validItemIds.contains(editItem.itemId())) {
                throw AiRecommendationExceptions.suggestionSchemaInvalid(
                        suggestion.schemaCode(), suggestion.schemaVersion(),
                        "Item ID not found: " + editItem.itemId());
            }
        }

        // Apply edits to items
        Map<UUID, Map<String, Object>> editMap = cmd.items().stream()
                .collect(Collectors.toMap(EditSuggestionCommand.EditItemCommand::itemId,
                        EditSuggestionCommand.EditItemCommand::proposedPayload));

        List<AiSuggestionItem> updatedItems = existingItems.stream().map(item -> {
            if (!editMap.containsKey(item.id())) {
                return item;
            }
            return new AiSuggestionItem(item.id(), item.suggestionId(), item.ordinal(), item.operation(),
                    item.targetEntityType(), item.targetEntityId(), item.expectedTargetVersionToken(),
                    item.schemaCode(), item.schemaVersion(), editMap.get(item.id()),
                    item.proposedPayload(), // preserve original as maskedBeforeSnapshot
                    item.payloadHash(), item.requiredTargetCapabilityCode(),
                    item.confirmationRequired(), item.baselineImpact(), item.createdAt());
        }).toList();

        itemRepository.saveAll(updatedItems);

        OffsetDateTime now = OffsetDateTime.now();
        AiSuggestion updated = withStatus(suggestion, SuggestionStatus.EDITED, now);
        updated = suggestionRepository.save(updated);

        saveReview(suggestion.id(), cmd.actorId(), ReviewDecision.EDIT,
                suggestion.status(), SuggestionStatus.EDITED, cmd.expectedVersion(),
                null, cmd.comment(), serializeItems(updatedItems), cmd.traceId());

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.EDIT_SUGGESTION, "Suggestion edited");

        return new EditSuggestionResponse(cmd.suggestionRef(), updated.status().name(),
                updated.editedAt(), updated.version());
    }

    private AiSuggestion withStatus(AiSuggestion s, SuggestionStatus status, OffsetDateTime now) {
        return new AiSuggestion(
                s.id(), s.runId(), s.policyId(), s.workspaceId(), s.projectId(), s.sourceSystem(),
                s.legacyPhase21SuggestionId(), s.packCode(), s.detectorCode(), s.suggestionType(),
                s.schemaCode(), s.schemaVersion(), s.category(), s.severity(), status,
                s.title(), s.summary(), s.reason(), s.targetEntityType(), s.targetEntityId(),
                s.targetVersionToken(), s.confidenceMethod(), s.confidenceValue(), s.confidenceLabel(),
                s.riskLevel(), s.dedupKey(), s.payloadHash(), s.occurrenceCount(),
                s.originConversationId(), s.originMessageId(), s.originTurnId(),
                s.supersedesSuggestionId(), s.supersededBySuggestionId(),
                s.firstObservedAt(), s.lastObservedAt(),
                s.viewedAt(), now, s.acceptedAt(), s.rejectedAt(), s.suppressedAt(),
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }

    private void saveReview(UUID suggestionId, UUID actorId, ReviewDecision decision,
                            SuggestionStatus from, SuggestionStatus to, long expectedVersion,
                            String reasonCode, String comment, String editedItemsJson, String traceId) {
        reviewRepository.save(new AiSuggestionReview(
                UUID.randomUUID(), suggestionId, actorId, decision, from, to,
                expectedVersion, reasonCode, comment, editedItemsJson, traceId, OffsetDateTime.now()));
    }

    private String serializeItems(List<AiSuggestionItem> items) {
        try {
            return objectMapper.writeValueAsString(items.stream()
                    .map(i -> Map.of("itemId", i.id(), "operation", i.operation()))
                    .toList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
