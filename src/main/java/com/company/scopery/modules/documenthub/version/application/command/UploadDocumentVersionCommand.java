package com.company.scopery.modules.documenthub.version.application.command;
import java.util.UUID;
public record UploadDocumentVersionCommand(UUID projectId, UUID documentId, String storageKey, String fileName, String contentType, Long fileSizeBytes, String checksum, String changeNotes) {}
