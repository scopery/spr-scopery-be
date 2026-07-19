package com.company.scopery.modules.documenthub.folder.application.command;
import java.util.UUID;
public record CreateDocumentFolderCommand(UUID projectId, UUID parentFolderId, String name, String description, Integer sortOrder) {}
