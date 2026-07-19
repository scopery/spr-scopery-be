package com.company.scopery.modules.trust.sensitiveaccesslog.http.request;
import jakarta.validation.constraints.NotBlank;
public record RecordSensitiveAccessRequest(@NotBlank String targetObjectType, String targetObjectId,
        String fieldPath, @NotBlank String classification, @NotBlank String accessAction, String reason) {}
