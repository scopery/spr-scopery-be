package com.company.scopery.modules.documenthub.version.http.request;
import jakarta.validation.constraints.NotBlank;
public record UploadDocumentVersionRequest(@NotBlank String storageKey, @NotBlank String fileName, String contentType, Long fileSizeBytes, String checksum, String changeNotes) {}
