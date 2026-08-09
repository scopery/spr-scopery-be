package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.suggestion.application.command.ApproveSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.application.service.SuggestionItemExecutorService;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("elicitationApproveSuggestionItemAction")
public class ApproveSuggestionItemAction {

    private static final Logger log = LoggerFactory.getLogger(ApproveSuggestionItemAction.class);

    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationSessionRepository sessionRepository;
    private final SuggestionItemExecutorService executorService;
    private final ElicitationActivityLogger activityLogger;

    public ApproveSuggestionItemAction(ElicitationSuggestionRepository suggestionRepository,
                                        ElicitationRoundRepository roundRepository,
                                        ElicitationSessionRepository sessionRepository,
                                        SuggestionItemExecutorService executorService,
                                        ElicitationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.roundRepository = roundRepository;
        this.sessionRepository = sessionRepository;
        this.executorService = executorService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSuggestionItemResponse execute(ApproveSuggestionItemCommand command) {
        ElicitationSuggestionItem item = suggestionRepository.findItemById(command.itemId())
                .orElseThrow(() -> ElicitationExceptions.suggestionItemNotFound(command.itemId()));

        if (item.status() != SuggestionItemStatus.PENDING) {
            throw ElicitationExceptions.suggestionItemNotPending(item.id());
        }

        ElicitationSuggestion suggestion = suggestionRepository.findById(item.suggestionId())
                .orElseThrow(() -> ElicitationExceptions.suggestionNotFound(item.suggestionId()));
        ElicitationRound round = roundRepository.findById(suggestion.roundId())
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(suggestion.roundId()));
        ElicitationSession session = sessionRepository.findById(round.sessionId())
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(round.sessionId()));

        UUID projectId = session.projectId();
        UUID scopePackageId = session.scopePackageId();
        UUID workspaceId = executorService.loadWorkspaceId(projectId);

        item.markApproved();
        item.markExecuting();
        ElicitationSuggestionItem executing = suggestionRepository.saveItem(item);

        try {
            String rollbackData = executorService.execute(executing, projectId, workspaceId, scopePackageId);

            executing.markSucceeded(rollbackData);
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
