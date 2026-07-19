package com.company.scopery.modules.aiplanning.suggestion.application.action;

import com.company.scopery.modules.aiplanning.apply.application.service.AiPlanningSafeApplyService;
import com.company.scopery.modules.aiplanning.apply.domain.enums.ApplyMode;
import com.company.scopery.modules.aiplanning.reviewaction.domain.enums.ReviewActionType;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewAction;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewActionRepository;
import com.company.scopery.modules.aiplanning.shared.activity.AiPlanningActivityLogger;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningActivityActions;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningEntityTypes;
import com.company.scopery.modules.aiplanning.shared.support.AiPlanningPlatformPublisher;
import com.company.scopery.modules.aiplanning.shared.util.AiPlanningEnumParser;
import com.company.scopery.modules.aiplanning.suggestion.application.command.ApplySuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.response.ApplySuggestionResponse;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplySuggestionAction {
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSafeApplyService safeApplyService;
    private final AiPlanningReviewActionRepository reviewActions;
    private final AiPlanningAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final AiPlanningPlatformPublisher publisher;
    private final AiPlanningActivityLogger activityLogger;

    public ApplySuggestionAction(AiPlanningSuggestionRepository suggestions,
                                 AiPlanningSafeApplyService safeApplyService,
                                 AiPlanningReviewActionRepository reviewActions,
                                 AiPlanningAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser,
                                 AiPlanningPlatformPublisher publisher,
                                 AiPlanningActivityLogger activityLogger) {
        this.suggestions = suggestions; this.safeApplyService = safeApplyService;
        this.reviewActions = reviewActions; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ApplySuggestionResponse execute(ApplySuggestionCommand command) {
        authorization.requireApply(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        ApplyMode mode = command.applyMode() == null || command.applyMode().isBlank()
                ? ApplyMode.ALL_ACCEPTED_ITEMS
                : AiPlanningEnumParser.parseRequired(ApplyMode.class, command.applyMode(), "applyMode");
        boolean requireCr = command.requireChangeRequestIfBaselined() == null
                || command.requireChangeRequestIfBaselined();
        var outcome = safeApplyService.applySuggestion(
                command.projectId(), command.suggestionId(), actor.id(), mode, requireCr);
        reviewActions.save(AiPlanningReviewAction.create(
                command.suggestionId(), null, ReviewActionType.APPLY, actor.id(), null, MDC.get("traceId")));
        suggestions.findById(command.suggestionId()).ifPresent(s ->
                publisher.enqueueSuggestion(s, "AI_PLANNING_SUGGESTION_APPLIED"));
        activityLogger.logSuccess(AiPlanningEntityTypes.SUGGESTION, command.suggestionId(),
                AiPlanningActivityActions.SUGGESTION_APPLIED, "Suggestion apply executed");
        return ApplySuggestionResponse.of(
                outcome.suggestionId(),
                outcome.changeRequestCreated(),
                outcome.changeRequestId(),
                outcome.results());
    }
}
