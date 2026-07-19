package com.company.scopery.modules.documenthub.attachment.application.action;

import com.company.scopery.modules.documenthub.attachment.application.command.CompleteAttachmentUploadCommand;
import com.company.scopery.modules.documenthub.attachment.application.response.DocumentAttachmentResponse;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachment;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachmentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.knowledge.shared.storage.StoredObjectMetadata;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CompleteAttachmentUploadAction {

    private final DocumentAttachmentRepository attachmentRepo;
    private final ObjectStorageProvider storage;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public CompleteAttachmentUploadAction(DocumentAttachmentRepository attachmentRepo,
                                           ObjectStorageProvider storage,
                                           DocumentHubAuthorizationService authorization,
                                           DocumentHubActivityLogger activityLogger) {
        this.attachmentRepo = attachmentRepo;
        this.storage = storage;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentAttachmentResponse execute(CompleteAttachmentUploadCommand c) {
        authorization.requireUpdate(c.projectId());

        DocumentAttachment attachment = attachmentRepo.findByIdAndDocumentId(c.attachmentId(), c.documentId())
                .orElseThrow(() -> DocumentHubExceptions.attachmentNotFound(c.attachmentId()));

        if (attachment.storageStatus().name().equals("AVAILABLE")) {
            return DocumentAttachmentResponse.from(attachment);
        }

        StoredObjectMetadata meta = storage.head(attachment.objectKey());
        DocumentAttachment updated;
        if (meta != null) {
            updated = attachmentRepo.save(attachment.markAvailable(meta.etag(), meta.sizeBytes()));
            activityLogger.logSuccess(DocumentHubEntityTypes.ATTACHMENT, attachment.id(),
                    DocumentHubActivityActions.ATTACHMENT_UPLOAD_COMPLETED,
                    "Upload completed: " + attachment.fileName());
        } else {
            updated = attachmentRepo.save(attachment.markFailed());
        }

        return DocumentAttachmentResponse.from(updated);
    }
}
