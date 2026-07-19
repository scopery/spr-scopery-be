package com.company.scopery.modules.documenthub.suggestion.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.action.SaveDocumentContentAction;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.suggestion.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.response.SuggestionResponse;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperationRepository;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component("documentHubAcceptSuggestionAction")
public class AcceptSuggestionAction {

    private final DocumentRepository documents;
    private final DocumentSuggestionRepository suggestionRepo;
    private final DocumentSuggestionOperationRepository operationRepo;
    private final SaveDocumentContentAction saveContent;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public AcceptSuggestionAction(DocumentRepository documents,
                                   DocumentSuggestionRepository suggestionRepo,
                                   DocumentSuggestionOperationRepository operationRepo,
                                   SaveDocumentContentAction saveContent,
                                   DocumentHubAuthorizationService authorization,
                                   DocumentHubActivityLogger activityLogger,
                                   TransactionalOutboxService outbox) {
        this.documents = documents;
        this.suggestionRepo = suggestionRepo;
        this.operationRepo = operationRepo;
        this.saveContent = saveContent;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public SuggestionResponse execute(AcceptSuggestionCommand c) {
        authorization.requireUpdate(c.projectId());

        var document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        var suggestion = suggestionRepo.findByIdAndDocumentId(c.suggestionId(), c.documentId())
                .orElseThrow(() -> DocumentHubExceptions.suggestionNotFound(c.suggestionId()));

        // BR-NDE-033: accept must check targetRevisionNo matches current
        if (suggestion.targetRevisionNo() != document.currentContentRevisionNo()) {
            throw DocumentHubExceptions.suggestionBaseRevisionConflict(
                    suggestion.targetRevisionNo(), document.currentContentRevisionNo());
        }

        // Load operations and apply them to produce new AST
        var operations = operationRepo.findBySuggestionId(suggestion.id());
        String newAst = applyOperations(document.contentChecksum(), operations);

        // Single write path — saves revision with type AI_ACCEPT
        var contentResult = saveContent.execute(new SaveDocumentContentCommand(
                c.projectId(), c.documentId(), newAst,
                document.currentContentRevisionNo(), document.editorSchemaVersion(),
                RevisionType.AI_ACCEPT));

        UUID actorId = resolveActorId();
        var accepted = suggestionRepo.save(suggestion.accept(actorId, contentResult.revisionNo()));

        outbox.enqueue(DocumentHubEntityTypes.SUGGESTION, accepted.id(),
                DocumentHubOutboxEventCodes.DOCUMENT_SUGGESTION_ACCEPTED,
                Map.of("documentId", c.documentId(), "suggestionId", accepted.id(),
                        "resultRevisionNo", contentResult.revisionNo()));

        activityLogger.logSuccess(DocumentHubEntityTypes.SUGGESTION, accepted.id(),
                DocumentHubActivityActions.SUGGESTION_ACCEPTED, "Suggestion accepted");

        return SuggestionResponse.from(accepted);
    }

    private String applyOperations(String currentChecksum,
                                    java.util.List<com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperation> operations) {
        // Operations contain the target AST as value on the first INSERT/REPLACE operation
        // This is a simplified application — the actual AST transformation is handled client-side
        // and submitted as a full replacement AST in the operation value
        return operations.stream()
                .filter(op -> op.value() != null && !op.value().isBlank())
                .findFirst()
                .map(op -> op.value())
                .orElseThrow(() -> new IllegalStateException("Suggestion has no applicable AST operation"));
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
