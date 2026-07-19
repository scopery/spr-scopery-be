package com.company.scopery.modules.documenthub.version.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.version.application.command.CreatePresignedDownloadCommand;
import com.company.scopery.modules.documenthub.version.application.response.PresignedDownloadResponse;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatePresignedDownloadAction {

    private final DocumentVersionRepository versions;
    private final DocumentHubAuthorizationService authorization;
    private final ObjectStorageProvider storage;
    private final DocumentHubActivityLogger activityLogger;

    public CreatePresignedDownloadAction(DocumentVersionRepository versions,
                                         DocumentHubAuthorizationService authorization,
                                         ObjectStorageProvider storage,
                                         DocumentHubActivityLogger activityLogger) {
        this.versions = versions;
        this.authorization = authorization;
        this.storage = storage;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PresignedDownloadResponse execute(CreatePresignedDownloadCommand cmd) {
        authorization.requireView(cmd.projectId());

        var version = versions.findByIdAndProjectId(cmd.versionId(), cmd.projectId())
                .orElseThrow(() -> KnowledgeExceptions.documentStorageUploadNotFound(cmd.versionId()));

        if (!"AVAILABLE".equals(version.storageStatus())) {
            throw KnowledgeExceptions.documentStorageUploadNotComplete(version.storageKey());
        }

        var presigned = storage.createPresignedDownload(version.storageKey(), version.fileName());

        activityLogger.logSuccess(DocumentHubEntityTypes.VERSION, version.id(),
                DocumentHubActivityActions.VERSION_PRESIGNED_DOWNLOAD_CREATED,
                "Presigned download created for version: " + version.id());

        return new PresignedDownloadResponse(version.id(), presigned.downloadUrl(), version.storageKey(), presigned.expiresAt());
    }
}
