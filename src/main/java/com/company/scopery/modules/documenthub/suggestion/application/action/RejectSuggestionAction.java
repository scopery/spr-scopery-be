package com.company.scopery.modules.documenthub.suggestion.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.suggestion.application.command.RejectSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.response.SuggestionResponse;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component("documentHubRejectSuggestionAction")
public class RejectSuggestionAction {

    private final DocumentSuggestionRepository suggestionRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public RejectSuggestionAction(DocumentSuggestionRepository suggestionRepo,
                                   DocumentHubAuthorizationService authorization,
                                   DocumentHubActivityLogger activityLogger,
                                   TransactionalOutboxService outbox) {
        this.suggestionRepo = suggestionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public SuggestionResponse execute(RejectSuggestionCommand c) {
        authorization.requireUpdate(c.projectId());

        var suggestion = suggestionRepo.findByIdAndDocumentId(c.suggestionId(), c.documentId())
                .orElseThrow(() -> DocumentHubExceptions.suggestionNotFound(c.suggestionId()));

        UUID actorId = resolveActorId();
        var rejected = suggestionRepo.save(suggestion.reject(actorId));

        outbox.enqueue(DocumentHubEntityTypes.SUGGESTION, rejected.id(),
                DocumentHubOutboxEventCodes.DOCUMENT_SUGGESTION_REJECTED,
                Map.of("documentId", c.documentId(), "suggestionId", rejected.id()));

        activityLogger.logSuccess(DocumentHubEntityTypes.SUGGESTION, rejected.id(),
                DocumentHubActivityActions.SUGGESTION_REJECTED, "Suggestion rejected");

        return SuggestionResponse.from(rejected);
    }

    private UUID resolveActorId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String userId) {
                return UUID.fromString(userId);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
