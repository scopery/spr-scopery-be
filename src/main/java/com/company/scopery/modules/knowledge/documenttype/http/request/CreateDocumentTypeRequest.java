package com.company.scopery.modules.knowledge.documenttype.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateDocumentTypeRequest(
        @NotBlank @Size(min = 2, max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        UUID workspaceId) {}
