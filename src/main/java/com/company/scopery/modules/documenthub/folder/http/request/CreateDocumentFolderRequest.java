package com.company.scopery.modules.documenthub.folder.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateDocumentFolderRequest(UUID parentFolderId, @NotBlank String name, String description, Integer sortOrder) {}
