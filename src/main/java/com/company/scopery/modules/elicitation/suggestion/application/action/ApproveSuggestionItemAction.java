package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.suggestion.application.command.ApproveSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("elicitationApproveSuggestionItemAction")
public class ApproveSuggestionItemAction {

    private static final Logger log = LoggerFactory.getLogger(ApproveSuggestionItemAction.class);

    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationActivityLogger activityLogger;

    public ApproveSuggestionItemAction(ElicitationSuggestionRepository suggestionRepository,
                                        ElicitationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSuggestionItemResponse execute(ApproveSuggestionItemCommand command) {
        ElicitationSuggestionItem item = suggestionRepository.findItemById(command.itemId())
                .orElseThrow(() -> ElicitationExceptions.suggestionItemNotFound(command.itemId()));

        if (item.status() != SuggestionItemStatus.PENDING) {
            throw ElicitationExceptions.suggestionItemNotPending(item.id());
        }

        item.markApproved();
        item.markExecuting();

        ElicitationSuggestionItem executing = suggestionRepository.saveItem(item);

        try {
            // Execute the action — currently a stub; full execution dispatched by action code in future phase
            log.info("Executing suggestion item action='{}' targetType='{}' targetId='{}'",
                    executing.action(), executing.targetEntityType(), executing.targetEntityId());

            executing.markSucceeded(null);
            ElicitationSuggestionItem saved = suggestionRepository.saveItem(executing);

            activityLogger.logSuccess(
                    ElicitationEntityTypes.SUGGESTION_ITEM,
                    saved.id(),
                    ElicitationActivityActions.APPROVE_SUGGESTION_ITEM,
                    "Suggestion item approved and executed: " + saved.action()
            );

            return ElicitationSuggestionItemResponse.from(saved);
        } catch (Exception e) {
            log.error("Suggestion item execution failed for item {}: {}", executing.id(), e.getMessage());
            executing.markFailed(e.getMessage());
            ElicitationSuggestionItem failed = suggestionRepository.saveItem(executing);
            throw ElicitationExceptions.suggestionItemExecutionFailed(failed.id(), e.getMessage());
        }
    }
}
