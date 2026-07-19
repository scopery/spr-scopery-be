package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ToggleClientVisibilityAction {

    private final DocumentRepository documentRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public ToggleClientVisibilityAction(DocumentRepository documentRepo,
                                         DocumentHubAuthorizationService authorization,
                                         DocumentHubActivityLogger activityLogger) {
        this.documentRepo = documentRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentResponse execute(UUID projectId, UUID documentId, boolean clientVisible) {
        authorization.requireUpdate(projectId);

        var document = documentRepo.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(documentId));

        if (clientVisible && "RESTRICTED".equalsIgnoreCase(document.classification())) {
            throw DocumentHubExceptions.clientVisibilityInvalid(1);
        }

        var saved = documentRepo.save(document.withClientVisible(clientVisible));

        activityLogger.logSuccess(DocumentHubEntityTypes.DOCUMENT, saved.id(),
                DocumentHubActivityActions.CONTENT_MODE_CHANGED,
                "Client visibility set to " + clientVisible);

        return DocumentResponse.from(saved);
    }
}
