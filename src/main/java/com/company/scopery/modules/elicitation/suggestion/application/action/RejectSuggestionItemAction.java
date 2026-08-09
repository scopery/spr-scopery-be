package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.suggestion.application.command.RejectSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("elicitationRejectSuggestionItemAction")
public class RejectSuggestionItemAction {

    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationActivityLogger activityLogger;

    public RejectSuggestionItemAction(ElicitationSuggestionRepository suggestionRepository,
                                       ElicitationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSuggestionItemResponse execute(RejectSuggestionItemCommand command) {
        ElicitationSuggestionItem item = suggestionRepository.findItemById(command.itemId())
                .orElseThrow(() -> ElicitationExceptions.suggestionItemNotFound(command.itemId()));

        if (item.status() != SuggestionItemStatus.PENDING) {
            throw ElicitationExceptions.suggestionItemNotPending(item.id());
        }

        item.markRejected();
        ElicitationSuggestionItem saved = suggestionRepository.saveItem(item);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SUGGESTION_ITEM,
                saved.id(),
                ElicitationActivityActions.REJECT_SUGGESTION_ITEM,
                "Suggestion item rejected: " + saved.action()
        );

        return ElicitationSuggestionItemResponse.from(saved);
    }
}
