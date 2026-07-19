package com.company.scopery.modules.documenthub.document.application.command;
import java.util.UUID;
public record CreateDocumentCommand(UUID projectId, UUID folderId, String documentTypeCode, String code, String title, String description, String contentMode) {}
