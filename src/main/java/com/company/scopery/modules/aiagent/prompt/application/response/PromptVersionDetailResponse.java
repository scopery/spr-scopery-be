package com.company.scopery.modules.aiagent.prompt.application.response;

import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;

import java.time.Instant;
import java.util.UUID;

public record PromptVersionDetailResponse(
        UUID id,
        UUID templateId,
        String templateCode,
        int versionNumber,
        String title,
        String content,
        String contentFormat,
        String variableSchema,
        String changeNote,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static PromptVersionDetailResponse from(PromptVersion version, String templateCode) {
        return new PromptVersionDetailResponse(
                version.id(),
                version.templateId(),
                templateCode,
                version.versionNumber(),
                version.title(),
                version.content(),
                version.contentFormat().name(),
                version.variableSchema(),
                version.changeNote(),
                version.status().name(),
                version.createdAt(),
                version.updatedAt()
        );
    }
}