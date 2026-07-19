package com.company.scopery.modules.documenthub.version.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.version.application.command.CreatePresignedUploadCommand;
import com.company.scopery.modules.documenthub.version.application.response.PresignedUploadResponse;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersion;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageConfig;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreatePresignedUploadAction {

    private final ProjectRepository projects;
    private final DocumentVersionRepository versions;
    private final DocumentHubAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ObjectStorageProvider storage;
    private final ObjectStorageConfig storageConfig;
    private final DocumentHubActivityLogger activityLogger;

    public CreatePresignedUploadAction(ProjectRepository projects, DocumentVersionRepository versions,
                                       DocumentHubAuthorizationService authorization,
                                       CurrentUserAuthorizationService currentUser,
                                       ObjectStorageProvider storage, ObjectStorageConfig storageConfig,
                                       DocumentHubActivityLogger activityLogger) {
        this.projects = projects;
        this.versions = versions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.storage = storage;
        this.storageConfig = storageConfig;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PresignedUploadResponse execute(CreatePresignedUploadCommand cmd) {
        authorization.requireCreate(cmd.projectId());
        var project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw DocumentHubExceptions.projectArchived(cmd.projectId());
        }

        UUID versionId = UUID.randomUUID();
        String storageKey = buildObjectKey(project.workspaceId(), cmd.projectId(), cmd.documentId(), versionId);
        String storageProvider = resolveStorageProvider();
        int nextVersion = versions.nextVersionNumber(cmd.documentId());

        var version = DocumentVersion.createForPresignedUpload(
                cmd.documentId(), cmd.projectId(), project.workspaceId(),
                nextVersion, storageKey, cmd.fileName(), cmd.contentType(),
                storageProvider, cmd.changeNotes(),
                currentUser.resolveCurrentUser().id());

        var saved = versions.save(withId(version, versionId));

        var presigned = storage.createPresignedUpload(
                storageKey, cmd.contentType(), storageConfig.getMaxUploadSizeBytes());

        activityLogger.logSuccess(com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes.VERSION,
                saved.id(), DocumentHubActivityActions.VERSION_PRESIGNED_UPLOAD_CREATED,
                "Presigned upload created for version: " + saved.id());

        return new PresignedUploadResponse(saved.id(), presigned.uploadUrl(), storageKey, storageProvider, presigned.expiresAt());
    }

    private String buildObjectKey(UUID workspaceId, UUID projectId, UUID documentId, UUID versionId) {
        return "workspaces/" + workspaceId + "/projects/" + projectId +
               "/documents/" + documentId + "/versions/" + versionId + "/" + UUID.randomUUID();
    }

    private String resolveStorageProvider() {
        String provider = storageConfig.getProvider();
        return (provider != null && provider.equalsIgnoreCase("s3-compatible")) ? "MINIO" : "MINIO";
    }

    private DocumentVersion withId(DocumentVersion v, UUID id) {
        return new DocumentVersion(id, v.documentId(), v.projectId(), v.workspaceId(),
                v.versionNumber(), v.storageKey(), v.fileName(), v.contentType(),
                v.fileSizeBytes(), v.checksum(), v.status(), v.changeNotes(),
                v.uploadedBy(), v.uploadedAt(),
                v.storageProvider(), v.storageStatus(), v.storageEtag(),
                v.uploadCompletedAt(), v.storageVerifiedAt(),
                v.version(), v.createdAt(), v.updatedAt());
    }
}
