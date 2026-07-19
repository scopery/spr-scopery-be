package com.company.scopery.modules.documenthub.folder.application.command;

import java.util.UUID;

public record ArchiveDocumentFolderCommand(UUID projectId, UUID folderId) {}
