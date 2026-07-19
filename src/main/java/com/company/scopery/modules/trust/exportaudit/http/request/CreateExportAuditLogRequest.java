package com.company.scopery.modules.trust.exportaudit.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateExportAuditLogRequest(@NotBlank String exportType, String targetObjectType,
        @NotBlank String classification, String reason) {}
