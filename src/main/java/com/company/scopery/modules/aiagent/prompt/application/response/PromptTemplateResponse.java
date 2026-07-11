package com.company.scopery.modules.aiagent.prompt.application.response;

import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;

import java.time.Instant;
import java.util.UUID;

public record PromptTemplateResponse(
        UUID id,
        UUID agentId,
        String name,
        String code,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static PromptTemplateResponse from(PromptTemplate template) {
        return new PromptTemplateResponse(
                template.id(),
                template.agentId(),
                template.name(),
                template.code().value(),
                template.description(),
                template.status().name(),
                template.createdAt(),
                template.updatedAt()
        );
    }
}
