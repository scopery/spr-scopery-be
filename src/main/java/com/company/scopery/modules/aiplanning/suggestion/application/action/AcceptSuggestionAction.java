package com.company.scopery.modules.aiplanning.suggestion.application.action;

import com.company.scopery.modules.aiplanning.reviewaction.domain.enums.ReviewActionType;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewAction;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewActionRepository;
import com.company.scopery.modules.aiplanning.shared.activity.AiPlanningActivityLogger;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningActivityActions;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningEntityTypes;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.shared.support.AiPlanningPlatformPublisher;
import com.company.scopery.modules.aiplanning.suggestion.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.response.AiPlanningSuggestionResponse;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("aiPlanningAcceptSuggestionAction")
public class AcceptSuggestionAction {
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSuggestionItemRepository items;
    private final AiPlanningReviewActionRepository reviewActions;
    private final AiPlanningAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final AiPlanningPlatformPublisher publisher;
    private final AiPlanningActivityLogger activityLogger;

    public AcceptSuggestionAction(AiPlanningSuggestionRepository suggestions,
                                  AiPlanningSuggestionItemRepository items,
                                  AiPlanningReviewActionRepository reviewActions,
                                  AiPlanningAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser,
                                  AiPlanningPlatformPublisher publisher,
                                  AiPlanningActivityLogger activityLogger) {
        this.suggestions = suggestions; this.items = items; this.reviewActions = reviewActions;
        this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public AiPlanningSuggestionResponse execute(AcceptSuggestionCommand command) {
        authorization.requireAccept(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var suggestion = suggestions.findByIdAndProjectId(command.suggestionId(), command.projectId())
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(command.suggestionId()));
        try {
            suggestion = suggestions.save(suggestion.accept(actor.id()));
        } catch (IllegalStateException ex) {
            throw AiPlanningExceptions.invalidStatus(ex.getMessage());
        }
        for (var item : items.findBySuggestionId(command.suggestionId())) {
            if (item.status().name().equals("PROPOSED")) {
                items.save(item.accept(actor.id()));
            }
        }
        reviewActions.save(AiPlanningReviewAction.create(
                suggestion.id(), null, ReviewActionType.ACCEPT, actor.id(), null, MDC.get("traceId")));
        publisher.enqueueSuggestion(suggestion, "AI_PLANNING_SUGGESTION_ACCEPTED");
        activityLogger.logSuccess(AiPlanningEntityTypes.SUGGESTION, suggestion.id(),
                AiPlanningActivityActions.SUGGESTION_ACCEPTED, "Suggestion accepted");
        return AiPlanningSuggestionResponse.from(suggestion);
    }
}
