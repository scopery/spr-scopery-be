package com.company.scopery.modules.documenthub.version.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.version.application.command.CompleteUploadCommand;
import com.company.scopery.modules.documenthub.version.application.response.DocumentVersionResponse;
import com.company.scopery.modules.documenthub.version.domain.enums.DocumentVersionStatus;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersion;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class CompleteUploadAction {

    private final DocumentVersionRepository versions;
    private final DocumentHubAuthorizationService authorization;
    private final ObjectStorageProvider storage;
    private final DocumentHubActivityLogger activityLogger;

    public CompleteUploadAction(DocumentVersionRepository versions,
                                DocumentHubAuthorizationService authorization,
                                ObjectStorageProvider storage,
                                DocumentHubActivityLogger activityLogger) {
        this.versions = versions;
        this.authorization = authorization;
        this.storage = storage;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentVersionResponse execute(CompleteUploadCommand cmd) {
        authorization.requireCreate(cmd.projectId());

        var version = versions.findByIdAndProjectId(cmd.versionId(), cmd.projectId())
                .orElseThrow(() -> KnowledgeExceptions.documentStorageUploadNotFound(cmd.versionId()));

        if (!"PENDING_UPLOAD".equals(version.storageStatus())) {
            throw KnowledgeExceptions.documentStorageUploadNotFound(cmd.versionId());
        }

        var meta = storage.head(version.storageKey());
        if (meta == null) {
            throw KnowledgeExceptions.documentStorageUploadNotComplete(version.storageKey());
        }

        if (cmd.checksum() != null && !cmd.checksum().isBlank()
                && meta.etag() != null && !meta.etag().replace("\"", "").equalsIgnoreCase(cmd.checksum())) {
            throw KnowledgeExceptions.documentStorageObjectMismatch(
                    "Provided checksum does not match stored object ETag");
        }

        Instant now = Instant.now();
        var updated = new DocumentVersion(
                version.id(), version.documentId(), version.projectId(), version.workspaceId(),
                version.versionNumber(), version.storageKey(), version.fileName(), version.contentType(),
                meta.sizeBytes(), cmd.checksum() != null ? cmd.checksum() : meta.etag(),
                DocumentVersionStatus.CURRENT, version.changeNotes(), version.uploadedBy(), version.uploadedAt(),
                version.storageProvider(), "AVAILABLE",
                meta.etag(), now, now,
                version.version(), version.createdAt(), now);

        var saved = versions.save(updated);
        activityLogger.logSuccess(DocumentHubEntityTypes.VERSION, saved.id(),
                DocumentHubActivityActions.VERSION_UPLOAD_COMPLETED, "Upload completed for version: " + saved.id());
        return DocumentVersionResponse.from(saved);
    }
}
