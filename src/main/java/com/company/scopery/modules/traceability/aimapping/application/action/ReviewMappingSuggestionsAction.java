package com.company.scopery.modules.traceability.aimapping.application.action;

import com.company.scopery.modules.traceability.aimapping.application.command.ReviewSuggestionsCommand;
import com.company.scopery.modules.traceability.aimapping.shared.activity.AiMappingActivityLogger;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingActivityActions;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingEntityTypes;
import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReviewMappingSuggestionsAction {

    private final MappingSuggestionRepository suggestionRepository;
    private final AiMappingActivityLogger activityLogger;

    public ReviewMappingSuggestionsAction(MappingSuggestionRepository suggestionRepository,
                                          AiMappingActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(ReviewSuggestionsCommand command) {
        List<UUID> toAccept = new ArrayList<>();
        List<UUID> toReject = new ArrayList<>();

        List<UUID> allIds = command.decisions().stream()
                .map(ReviewSuggestionsCommand.SuggestionDecision::suggestionId)
                .toList();

        List<MappingSuggestion> suggestions = suggestionRepository.findAllById(allIds);

        for (MappingSuggestion s : suggestions) {
            if (s.reviewStatus() != SuggestionReviewStatus.PENDING) {
                throw AiMappingExceptions.suggestionInvalidStatus(
                        s.id(), s.reviewStatus().name(), SuggestionReviewStatus.PENDING.name());
            }
        }

        for (ReviewSuggestionsCommand.SuggestionDecision d : command.decisions()) {
            if ("ACCEPT".equalsIgnoreCase(d.decision())) {
                toAccept.add(d.suggestionId());
            } else if ("REJECT".equalsIgnoreCase(d.decision())) {
                toReject.add(d.suggestionId());
            }
        }

        Instant now = Instant.now();

        if (!toAccept.isEmpty()) {
            suggestionRepository.bulkUpdateReviewStatus(toAccept, SuggestionReviewStatus.ACCEPTED,
                    command.reviewedBy(), now);
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_SUGGESTION,
                    AiMappingActivityActions.ACCEPT_SUGGESTION,
                    "Bulk accepted " + toAccept.size() + " suggestions");
        }

        if (!toReject.isEmpty()) {
            suggestionRepository.bulkUpdateReviewStatus(toReject, SuggestionReviewStatus.REJECTED,
                    command.reviewedBy(), now);
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_SUGGESTION,
                    AiMappingActivityActions.REJECT_SUGGESTION,
                    "Bulk rejected " + toReject.size() + " suggestions");
        }
    }
}
