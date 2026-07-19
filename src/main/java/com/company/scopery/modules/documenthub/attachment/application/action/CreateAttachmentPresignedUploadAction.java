package com.company.scopery.modules.documenthub.attachment.application.action;

import com.company.scopery.modules.documenthub.attachment.application.command.CreateAttachmentPresignedUploadCommand;
import com.company.scopery.modules.documenthub.attachment.application.response.DocumentAttachmentResponse;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachment;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachmentRepository;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.knowledge.shared.storage.PresignedUpload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateAttachmentPresignedUploadAction {

    private static final long MAX_ATTACHMENT_BYTES = 50 * 1024 * 1024L; // 50 MB
    private static final String ATTACHMENT_OBJECT_PREFIX = "document-attachments/";

    private final DocumentRepository documents;
    private final DocumentAttachmentRepository attachmentRepo;
    private final ObjectStorageProvider storage;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public CreateAttachmentPresignedUploadAction(DocumentRepository documents,
                                                  DocumentAttachmentRepository attachmentRepo,
                                                  ObjectStorageProvider storage,
                                                  DocumentHubAuthorizationService authorization,
                                                  DocumentHubActivityLogger activityLogger) {
        this.documents = documents;
        this.attachmentRepo = attachmentRepo;
        this.storage = storage;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentAttachmentResponse execute(CreateAttachmentPresignedUploadCommand c) {
        authorization.requireUpdate(c.projectId());

        var document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        if (document.isArchived()) {
            throw DocumentHubExceptions.documentArchivedForEdit(c.documentId());
        }

        String objectKey = ATTACHMENT_OBJECT_PREFIX + c.documentId() + "/" + UUID.randomUUID() + "/" + c.fileName();
        String contentType = c.mediaType() != null ? c.mediaType() : "application/octet-stream";
        long maxSize = c.fileSizeBytes() != null ? Math.min(c.fileSizeBytes(), MAX_ATTACHMENT_BYTES) : MAX_ATTACHMENT_BYTES;

        PresignedUpload upload = storage.createPresignedUpload(objectKey, contentType, maxSize);

        DocumentAttachment attachment = DocumentAttachment.createPendingUpload(
                c.documentId(), document.workspaceId(), c.projectId(),
                c.blockId(), c.fileName(), contentType, c.fileSizeBytes(),
                upload.objectKey(), upload.uploadUrl(), upload.expiresAt());

        DocumentAttachment saved = attachmentRepo.save(attachment);

        activityLogger.logSuccess(DocumentHubEntityTypes.ATTACHMENT, saved.id(),
                DocumentHubActivityActions.ATTACHMENT_PRESIGNED_UPLOAD_CREATED,
                "Presigned upload created for " + c.fileName());

        return DocumentAttachmentResponse.from(saved);
    }
}
