package com.company.scopery.modules.documenthub.template.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record InstantiateNativeTemplateRequest(
        @NotNull UUID projectId,
        @NotNull UUID targetDocumentId,
        Map<String, String> variables
) {}
