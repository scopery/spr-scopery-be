package com.company.scopery.modules.documenthub.folder.domain.model;
import com.company.scopery.modules.documenthub.folder.domain.enums.FolderStatus;
import java.time.Instant; import java.util.UUID;
public record DocumentFolder(UUID id, UUID workspaceId, UUID projectId, UUID parentFolderId, String name, String description,
                             FolderStatus status, Integer sortOrder, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static DocumentFolder create(UUID workspaceId, UUID projectId, UUID parentFolderId, String name, String description, Integer sortOrder) {
        Instant now = Instant.now();
        return new DocumentFolder(UUID.randomUUID(), workspaceId, projectId, parentFolderId, name, description, FolderStatus.ACTIVE, sortOrder, null, null, 0, now, now);
    }
    public DocumentFolder archive(UUID actorId) {
        if (status == FolderStatus.ARCHIVED) {
            throw new IllegalStateException("Folder is already archived");
        }
        return new DocumentFolder(id, workspaceId, projectId, parentFolderId, name, description, FolderStatus.ARCHIVED, sortOrder, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
