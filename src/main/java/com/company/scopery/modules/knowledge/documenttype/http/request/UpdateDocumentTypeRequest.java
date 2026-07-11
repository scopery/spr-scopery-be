package com.company.scopery.modules.knowledge.documenttype.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDocumentTypeRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description) {}
