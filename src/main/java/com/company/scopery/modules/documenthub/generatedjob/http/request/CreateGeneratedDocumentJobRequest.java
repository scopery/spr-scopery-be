package com.company.scopery.modules.documenthub.generatedjob.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateGeneratedDocumentJobRequest(UUID templateId, UUID templateVersionId, @NotBlank String jobType, String sourceType, UUID sourceId) {}
