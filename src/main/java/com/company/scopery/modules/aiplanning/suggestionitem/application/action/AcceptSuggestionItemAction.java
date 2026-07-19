package com.company.scopery.modules.aiplanning.suggestionitem.application.action;

import com.company.scopery.modules.aiplanning.reviewaction.domain.enums.ReviewActionType;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewAction;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewActionRepository;
import com.company.scopery.modules.aiplanning.shared.activity.AiPlanningActivityLogger;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningActivityActions;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningEntityTypes;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.application.command.AcceptSuggestionItemCommand;
import com.company.scopery.modules.aiplanning.suggestionitem.application.response.AiPlanningSuggestionItemResponse;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AcceptSuggestionItemAction {
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSuggestionItemRepository items;
    private final AiPlanningReviewActionRepository reviewActions;
    private final AiPlanningAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final AiPlanningActivityLogger activityLogger;

    public AcceptSuggestionItemAction(AiPlanningSuggestionRepository suggestions,
                                      AiPlanningSuggestionItemRepository items,
                                      AiPlanningReviewActionRepository reviewActions,
                                      AiPlanningAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser,
                                      AiPlanningActivityLogger activityLogger) {
        this.suggestions = suggestions; this.items = items; this.reviewActions = reviewActions;
        this.authorization = authorization; this.currentUser = currentUser; this.activityLogger = activityLogger;
    }

    @Transactional
    public AiPlanningSuggestionItemResponse execute(AcceptSuggestionItemCommand command) {
        authorization.requireAccept(command.projectId());
        suggestions.findByIdAndProjectId(command.suggestionId(), command.projectId())
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(command.suggestionId()));
        var actor = currentUser.resolveCurrentUser();
        var item = items.findByIdAndSuggestionId(command.itemId(), command.suggestionId())
                .orElseThrow(() -> AiPlanningExceptions.itemNotFound(command.itemId()));
        try {
            item = items.save(item.accept(actor.id()));
        } catch (IllegalStateException ex) {
            throw AiPlanningExceptions.invalidStatus(ex.getMessage());
        }
        reviewActions.save(AiPlanningReviewAction.create(
                command.suggestionId(), command.itemId(), ReviewActionType.ACCEPT_ITEM, actor.id(), null, MDC.get("traceId")));
        activityLogger.logSuccess(AiPlanningEntityTypes.SUGGESTION_ITEM, item.id(),
                AiPlanningActivityActions.ITEM_ACCEPTED, "Suggestion item accepted");
        return AiPlanningSuggestionItemResponse.from(item);
    }
}
