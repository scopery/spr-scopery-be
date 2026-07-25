package com.company.scopery.modules.documenthub.document.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDocumentRequest(@NotBlank String title) {}
