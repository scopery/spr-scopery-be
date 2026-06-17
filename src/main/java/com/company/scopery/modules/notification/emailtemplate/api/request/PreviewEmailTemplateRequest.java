package com.company.scopery.modules.notification.emailtemplate.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PreviewEmailTemplateRequest(
        @NotNull UUID versionId,
        Map<String, Object> samplePayload
) {}
