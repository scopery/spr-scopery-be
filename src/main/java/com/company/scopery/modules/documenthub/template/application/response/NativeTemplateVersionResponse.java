package com.company.scopery.modules.documenthub.template.application.response;

import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersion;

import java.util.UUID;

public record NativeTemplateVersionResponse(UUID id, UUID templateId, Integer versionNumber, String status) {
    public static NativeTemplateVersionResponse from(DocumentTemplateVersion v) {
        return new NativeTemplateVersionResponse(v.id(), v.templateId(), v.versionNumber(), v.status());
    }
}
