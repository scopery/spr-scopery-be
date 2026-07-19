package com.company.scopery.modules.documenthub.suggestion.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.suggestion.application.command.CreateSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.response.SuggestionResponse;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperation;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperationRepository;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class CreateSuggestionAction {

    private final DocumentRepository documents;
    private final DocumentSuggestionRepository suggestionRepo;
    private final DocumentSuggestionOperationRepository operationRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public CreateSuggestionAction(DocumentRepository documents,
                                   DocumentSuggestionRepository suggestionRepo,
                                   DocumentSuggestionOperationRepository operationRepo,
                                   DocumentHubAuthorizationService authorization,
                                   DocumentHubActivityLogger activityLogger,
                                   TransactionalOutboxService outbox) {
        this.documents = documents;
        this.suggestionRepo = suggestionRepo;
        this.operationRepo = operationRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public SuggestionResponse execute(CreateSuggestionCommand c) {
        authorization.requireUpdate(c.projectId());

        var document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        // Validate targetRevisionNo matches current — must propose against exact current revision
        if (c.targetRevisionNo() != document.currentContentRevisionNo()) {
            throw DocumentHubExceptions.suggestionBaseRevisionConflict(c.targetRevisionNo(), document.currentContentRevisionNo());
        }

        DocumentSuggestion saved = suggestionRepo.save(
                DocumentSuggestion.create(c.documentId(), document.workspaceId(), c.projectId(),
                        c.targetRevisionNo(), c.description()));

        if (c.operations() != null && !c.operations().isEmpty()) {
            List<DocumentSuggestionOperation> ops = c.operations().stream()
                    .map(op -> DocumentSuggestionOperation.create(saved.id(), op.opType(),
                            op.blockId(), op.path(), op.value(), op.ordinal()))
                    .toList();
            operationRepo.saveAll(ops);
        }

        outbox.enqueue(DocumentHubEntityTypes.SUGGESTION, saved.id(),
                DocumentHubOutboxEventCodes.DOCUMENT_SUGGESTION_CREATED,
                Map.of("documentId", c.documentId(), "suggestionId", saved.id()));

        activityLogger.logSuccess(DocumentHubEntityTypes.SUGGESTION, saved.id(),
                DocumentHubActivityActions.SUGGESTION_CREATED, "Suggestion created for revision " + c.targetRevisionNo());

        return SuggestionResponse.from(saved);
    }
}
