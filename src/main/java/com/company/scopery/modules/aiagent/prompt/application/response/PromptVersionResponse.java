package com.company.scopery.modules.aiagent.prompt.application.response;

import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;

import java.time.Instant;
import java.util.UUID;

public record PromptVersionResponse(
        UUID id,
        UUID templateId,
        int versionNumber,
        String title,
        String contentFormat,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static PromptVersionResponse from(PromptVersion version) {
        return new PromptVersionResponse(
                version.id(),
                version.templateId(),
                version.versionNumber(),
                version.title(),
                version.contentFormat().name(),
                version.status().name(),
                version.createdAt(),
                version.updatedAt()
        );
    }
}