package com.company.scopery.modules.documenthub.document.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateDocumentRequest(@NotBlank String title, String code, String description, String documentTypeCode, UUID folderId, String contentMode) {}
