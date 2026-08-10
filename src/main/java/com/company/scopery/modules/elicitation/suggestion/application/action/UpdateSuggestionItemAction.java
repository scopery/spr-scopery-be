package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.suggestion.application.command.UpdateSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateSuggestionItemAction {

    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationActivityLogger activityLogger;

    public UpdateSuggestionItemAction(ElicitationSuggestionRepository suggestionRepository,
                                       ElicitationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSuggestionItemResponse execute(UpdateSuggestionItemCommand command) {
        UUID itemId = command.itemId();
        ElicitationSuggestionItem item = suggestionRepository.findItemById(itemId)
                .orElseThrow(() -> ElicitationExceptions.suggestionItemNotFound(itemId));

        if (item.status() != SuggestionItemStatus.PENDING && item.status() != SuggestionItemStatus.FAILED) {
            throw ElicitationExceptions.suggestionItemNotPending(itemId);
        }

        item.editChanges(command.changesJson());
        ElicitationSuggestionItem saved = suggestionRepository.saveItem(item);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SUGGESTION_ITEM,
                saved.id(),
                ElicitationActivityActions.EDIT_SUGGESTION_ITEM,
                "Suggestion item changes updated: " + saved.action()
        );

        return ElicitationSuggestionItemResponse.from(saved);
    }
}
