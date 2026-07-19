package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.ViewSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.port.Phase21SuggestionCompatibilityPort;
import com.company.scopery.modules.airecommendation.application.response.ViewSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationStalenessService;
import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.domain.policy.RecommendationLifecyclePolicy;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionReviewRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.activity.AiRecommendationActivityLogger;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationActivityActions;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class ViewSuggestionAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_VIEWED = "AI_SUGGESTION_VIEWED";

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final Phase21SuggestionCompatibilityPort phase21Port;
    private final RecommendationStalenessService stalenessService;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public ViewSuggestionAction(AiSuggestionRepository suggestionRepository,
                                 AiSuggestionReviewRepository reviewRepository,
                                 Phase21SuggestionCompatibilityPort phase21Port,
                                 RecommendationStalenessService stalenessService,
                                 TransactionalOutboxService outbox,
                                 AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.reviewRepository = reviewRepository;
        this.phase21Port = phase21Port;
        this.stalenessService = stalenessService;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ViewSuggestionResponse execute(ViewSuggestionCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());
        AiSuggestion suggestion = resolveSuggestion(ref, cmd.workspaceId());

        validateVersionMatch(suggestion, cmd.expectedVersion(), cmd.suggestionRef());

        // Revalidate staleness
        if (stalenessService.isStale(suggestion)) {
            suggestion = markStale(suggestion);
            throw AiRecommendationExceptions.suggestionStale(cmd.suggestionRef());
        }

        if (!RecommendationLifecyclePolicy.canTransitionTo(suggestion.status(), SuggestionStatus.VIEWED)) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "VIEW");
        }

        OffsetDateTime now = OffsetDateTime.now();
        AiSuggestion updated = withStatus(suggestion, SuggestionStatus.VIEWED, now);
        updated = suggestionRepository.save(updated);

        saveReview(suggestion.id(), cmd.actorId(), ReviewDecision.VIEW,
                suggestion.status(), SuggestionStatus.VIEWED, cmd.expectedVersion(), null, null, cmd.traceId());

        outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                EVENT_VIEWED, SOURCE_SYSTEM, 1,
                Map.of("suggestionRef", cmd.suggestionRef(), "actorId", cmd.actorId().toString()));

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.VIEW_SUGGESTION, "Suggestion viewed");

        return new ViewSuggestionResponse(cmd.suggestionRef(), updated.status().name(),
                updated.viewedAt(), updated.version());
    }

    private AiSuggestion resolveSuggestion(SuggestionRef ref, UUID workspaceId) {
        if (ref.isPhase43()) {
            return suggestionRepository.findById(ref.uuid())
                    .filter(s -> workspaceId.equals(s.workspaceId()))
                    .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(ref.toString()));
        }
        return phase21Port.getByLegacyId(ref.uuid(), null)
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(ref.toString()));
    }

    private void validateVersionMatch(AiSuggestion suggestion, long expectedVersion, String ref) {
        if (expectedVersion >= 0 && suggestion.version() != expectedVersion) {
            throw AiRecommendationExceptions.suggestionVersionConflict(ref, expectedVersion, suggestion.version());
        }
    }

    private AiSuggestion markStale(AiSuggestion suggestion) {
        OffsetDateTime now = OffsetDateTime.now();
        AiSuggestion stale = new AiSuggestion(
                suggestion.id(), suggestion.runId(), suggestion.policyId(),
                suggestion.workspaceId(), suggestion.projectId(), suggestion.sourceSystem(),
                suggestion.legacyPhase21SuggestionId(), suggestion.packCode(), suggestion.detectorCode(),
                suggestion.suggestionType(), suggestion.schemaCode(), suggestion.schemaVersion(),
                suggestion.category(), suggestion.severity(), SuggestionStatus.STALE,
                suggestion.title(), suggestion.summary(), suggestion.reason(),
                suggestion.targetEntityType(), suggestion.targetEntityId(), suggestion.targetVersionToken(),
                suggestion.confidenceMethod(), suggestion.confidenceValue(), suggestion.confidenceLabel(),
                suggestion.riskLevel(), suggestion.dedupKey(), suggestion.payloadHash(),
                suggestion.occurrenceCount(), suggestion.originConversationId(),
                suggestion.originMessageId(), suggestion.originTurnId(),
                suggestion.supersedesSuggestionId(), suggestion.supersededBySuggestionId(),
                suggestion.firstObservedAt(), suggestion.lastObservedAt(),
                suggestion.viewedAt(), suggestion.editedAt(), suggestion.acceptedAt(),
                suggestion.rejectedAt(), suggestion.suppressedAt(), suggestion.expiresAt(),
                now, "TARGET_ENTITY_CHANGED", suggestion.createdAt(), now, suggestion.version() + 1);
        return suggestionRepository.save(stale);
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
                status == SuggestionStatus.VIEWED ? now : s.viewedAt(),
                s.editedAt(), s.acceptedAt(), s.rejectedAt(), s.suppressedAt(),
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }

    private void saveReview(UUID suggestionId, UUID actorId, ReviewDecision decision,
                            SuggestionStatus from, SuggestionStatus to, long expectedVersion,
                            String reasonCode, String comment, String traceId) {
        reviewRepository.save(new AiSuggestionReview(
                UUID.randomUUID(), suggestionId, actorId, decision, from, to,
                expectedVersion, reasonCode, comment, null, traceId, OffsetDateTime.now()));
    }
}
