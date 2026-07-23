package com.company.scopery.modules.aiassistant.message.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SendMessageRequest(
        @NotBlank @Size(max = 8000) String content,
        @Size(max = 200) String idempotencyKey,
        String modelProvider,
        String modelName,
        String pageCode,
        String entityType,
        UUID entityId,
        UUID sourceProjectId
) {}
