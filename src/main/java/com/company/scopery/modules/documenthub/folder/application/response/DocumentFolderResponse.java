package com.company.scopery.modules.documenthub.folder.application.response;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolder;
import java.time.Instant; import java.util.UUID;
public record DocumentFolderResponse(UUID id, UUID projectId, UUID parentFolderId, String name, String status, Integer sortOrder, Instant createdAt) {
    public static DocumentFolderResponse from(DocumentFolder e) {
        return new DocumentFolderResponse(e.id(), e.projectId(), e.parentFolderId(), e.name(), e.status().name(), e.sortOrder(), e.createdAt());
    }
}
