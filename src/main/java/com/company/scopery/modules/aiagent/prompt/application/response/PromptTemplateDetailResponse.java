package com.company.scopery.modules.aiagent.prompt.application.response;

import com.company.scopery.modules.aiagent.prompt.domain.PromptTemplate;

import java.time.Instant;
import java.util.UUID;

public record PromptTemplateDetailResponse(
        UUID id,
        UUID agentId,
        String agentName,
        String name,
        String code,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static PromptTemplateDetailResponse from(PromptTemplate template, String agentName) {
        return new PromptTemplateDetailResponse(
                template.id(),
                template.agentId(),
                agentName,
                template.name(),
                template.code().value(),
                template.description(),
                template.status().name(),
                template.createdAt(),
                template.updatedAt()
        );
    }
}
