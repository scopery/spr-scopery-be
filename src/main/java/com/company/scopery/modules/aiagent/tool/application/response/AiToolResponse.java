package com.company.scopery.modules.aiagent.tool.application.response;

import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;

import java.time.Instant;
import java.util.UUID;

public record AiToolResponse(
        UUID id,
        String code,
        String name,
        String description,
        String category,
        String mutationType,
        boolean requiresHumanApproval,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiToolResponse from(AiTool tool) {
        return new AiToolResponse(
                tool.id(),
                tool.code().value(),
                tool.name(),
                tool.description(),
                tool.category(),
                tool.mutationType().name(),
                tool.requiresHumanApproval(),
                tool.status().name(),
                tool.createdAt(),
                tool.updatedAt()
        );
    }
}
