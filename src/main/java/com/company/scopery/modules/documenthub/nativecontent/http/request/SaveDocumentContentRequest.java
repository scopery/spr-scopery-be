package com.company.scopery.modules.documenthub.nativecontent.http.request;

import jakarta.validation.constraints.NotBlank;

public record SaveDocumentContentRequest(
        @NotBlank String ast,
        long expectedBaseRevisionNo,
        Integer schemaVersion,
        String revisionType
) {}
