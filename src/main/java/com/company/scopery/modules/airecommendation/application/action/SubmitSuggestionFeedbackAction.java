package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.SubmitFeedbackCommand;
import com.company.scopery.modules.airecommendation.application.response.FeedbackResponse;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionFeedback;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionFeedbackRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
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
public class SubmitSuggestionFeedbackAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_FEEDBACK = "AI_SUGGESTION_FEEDBACK_SUBMITTED";

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionFeedbackRepository feedbackRepository;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public SubmitSuggestionFeedbackAction(AiSuggestionRepository suggestionRepository,
                                           AiSuggestionFeedbackRepository feedbackRepository,
                                           TransactionalOutboxService outbox,
                                           AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.feedbackRepository = feedbackRepository;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FeedbackResponse execute(SubmitFeedbackCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        AiSuggestion suggestion = suggestionRepository.findById(ref.uuid())
                .filter(s -> cmd.workspaceId().equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(cmd.suggestionRef()));

        // Upsert: one feedback record per actor per suggestion
        AiSuggestionFeedback feedback = feedbackRepository
                .findBySuggestionAndActor(suggestion.id(), cmd.actorId())
                .map(existing -> new AiSuggestionFeedback(
                        existing.id(), existing.suggestionId(), existing.actorId(),
                        cmd.helpful(), cmd.reasonCode(), cmd.comment(), cmd.observedOutcome(),
                        existing.createdAt()))
                .orElseGet(() -> new AiSuggestionFeedback(
                        UUID.randomUUID(), suggestion.id(), cmd.actorId(),
                        cmd.helpful(), cmd.reasonCode(), cmd.comment(), cmd.observedOutcome(),
                        OffsetDateTime.now()));

        feedback = feedbackRepository.save(feedback);

        outbox.enqueue(AiRecommendationEntityTypes.FEEDBACK, feedback.id(),
                EVENT_FEEDBACK, SOURCE_SYSTEM, 1,
                Map.of("suggestionRef", cmd.suggestionRef(), "actorId", cmd.actorId().toString()));

        activityLogger.logSuccess(AiRecommendationEntityTypes.FEEDBACK, feedback.id(),
                AiRecommendationActivityActions.SUBMIT_FEEDBACK, "Feedback submitted");

        return new FeedbackResponse(feedback.id(), cmd.suggestionRef(), feedback.createdAt());
    }
}
