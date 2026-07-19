package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.response.AcceptSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationStalenessService;
import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
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

@Component("aiRecommendationAcceptSuggestionAction")
public class AcceptSuggestionAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_ACCEPTED = "AI_SUGGESTION_ACCEPTED";

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final RecommendationStalenessService stalenessService;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public AcceptSuggestionAction(AiSuggestionRepository suggestionRepository,
                                   AiSuggestionReviewRepository reviewRepository,
                                   RecommendationStalenessService stalenessService,
                                   TransactionalOutboxService outbox,
                                   AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.reviewRepository = reviewRepository;
        this.stalenessService = stalenessService;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AcceptSuggestionResponse execute(AcceptSuggestionCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        AiSuggestion suggestion = resolveSuggestion(ref, cmd.workspaceId());

        if (cmd.expectedVersion() >= 0 && suggestion.version() != cmd.expectedVersion()) {
            throw AiRecommendationExceptions.suggestionVersionConflict(
                    cmd.suggestionRef(), cmd.expectedVersion(), suggestion.version());
        }

        if (stalenessService.isStale(suggestion)) {
            throw AiRecommendationExceptions.suggestionStale(cmd.suggestionRef());
        }

        if (!RecommendationLifecyclePolicy.canTransitionTo(suggestion.status(), SuggestionStatus.ACCEPTED)) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "ACCEPT");
        }

        OffsetDateTime now = OffsetDateTime.now();
        AiSuggestion updated = withStatus(suggestion, SuggestionStatus.ACCEPTED, now);
        updated = suggestionRepository.save(updated);

        reviewRepository.save(new AiSuggestionReview(
                UUID.randomUUID(), suggestion.id(), cmd.actorId(),
                ReviewDecision.ACCEPT, suggestion.status(), SuggestionStatus.ACCEPTED,
                cmd.expectedVersion(), null, cmd.comment(), null, cmd.traceId(), now));

        outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                EVENT_ACCEPTED, SOURCE_SYSTEM, 1,
                Map.of("suggestionRef", cmd.suggestionRef(), "actorId", cmd.actorId().toString(),
                        "domainMutationPerformed", false));

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.ACCEPT_SUGGESTION, "Suggestion accepted");

        // Phase 43: Accept never mutates target domain state
        return new AcceptSuggestionResponse(cmd.suggestionRef(), updated.status().name(),
                updated.acceptedAt(), false, updated.version());
    }

    private AiSuggestion resolveSuggestion(SuggestionRef ref, UUID workspaceId) {
        return suggestionRepository.findById(ref.uuid())
                .filter(s -> workspaceId.equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(ref.toString()));
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
                s.viewedAt(), s.editedAt(), now, s.rejectedAt(), s.suppressedAt(),
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }
}
