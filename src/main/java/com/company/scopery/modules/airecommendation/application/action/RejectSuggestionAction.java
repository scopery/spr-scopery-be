package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.RejectSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.response.RejectSuggestionResponse;
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

@Component("aiRecommendationRejectSuggestionAction")
public class RejectSuggestionAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_REJECTED = "AI_SUGGESTION_REJECTED";

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public RejectSuggestionAction(AiSuggestionRepository suggestionRepository,
                                   AiSuggestionReviewRepository reviewRepository,
                                   TransactionalOutboxService outbox,
                                   AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.reviewRepository = reviewRepository;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RejectSuggestionResponse execute(RejectSuggestionCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        AiSuggestion suggestion = suggestionRepository.findById(ref.uuid())
                .filter(s -> cmd.workspaceId().equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(cmd.suggestionRef()));

        if (cmd.expectedVersion() >= 0 && suggestion.version() != cmd.expectedVersion()) {
            throw AiRecommendationExceptions.suggestionVersionConflict(
                    cmd.suggestionRef(), cmd.expectedVersion(), suggestion.version());
        }

        if (!RecommendationLifecyclePolicy.canTransitionTo(suggestion.status(), SuggestionStatus.REJECTED)) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "REJECT");
        }

        OffsetDateTime now = OffsetDateTime.now();
        AiSuggestion updated = withStatus(suggestion, SuggestionStatus.REJECTED, now);
        updated = suggestionRepository.save(updated);

        reviewRepository.save(new AiSuggestionReview(
                UUID.randomUUID(), suggestion.id(), cmd.actorId(),
                ReviewDecision.REJECT, suggestion.status(), SuggestionStatus.REJECTED,
                cmd.expectedVersion(), cmd.reasonCode(), cmd.comment(), null, cmd.traceId(), now));

        outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                EVENT_REJECTED, SOURCE_SYSTEM, 1,
                Map.of("suggestionRef", cmd.suggestionRef(), "actorId", cmd.actorId().toString()));

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.REJECT_SUGGESTION, "Suggestion rejected");

        return new RejectSuggestionResponse(cmd.suggestionRef(), updated.status().name(),
                updated.rejectedAt(), updated.version());
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
                s.viewedAt(), s.editedAt(), s.acceptedAt(), now, s.suppressedAt(),
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }
}
