package com.company.scopery.modules.documenthub.nativecontent.application.action;

import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.command.RestoreRevisionCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RestoreRevisionAction {

    private final DocumentRepository documents;
    private final DocumentRevisionRepository revisionRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final SaveDocumentContentAction saveContent;

    public RestoreRevisionAction(DocumentRepository documents,
                                  DocumentRevisionRepository revisionRepo,
                                  DocumentHubAuthorizationService authorization,
                                  DocumentHubActivityLogger activityLogger,
                                  SaveDocumentContentAction saveContent) {
        this.documents = documents;
        this.revisionRepo = revisionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.saveContent = saveContent;
    }

    @Transactional
    public DocumentContentResponse execute(RestoreRevisionCommand c) {
        authorization.requireUpdate(c.projectId());

        Document document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        DocumentRevision target = revisionRepo.findByDocumentIdAndRevisionNo(c.documentId(), c.revisionNo())
                .orElseThrow(() -> DocumentHubExceptions.contentNotFound(c.documentId()));

        activityLogger.logSuccess(DocumentHubEntityTypes.REVISION, c.documentId(),
                DocumentHubActivityActions.REVISION_RESTORED, "Restoring to revision " + c.revisionNo());

        // Delegate to single write path — uses current revisionNo as base, type = RESTORE
        return saveContent.execute(new SaveDocumentContentCommand(
                c.projectId(), c.documentId(),
                target.ast(),
                document.currentContentRevisionNo(),
                target.schemaVersion(),
                RevisionType.RESTORE));
    }
}
