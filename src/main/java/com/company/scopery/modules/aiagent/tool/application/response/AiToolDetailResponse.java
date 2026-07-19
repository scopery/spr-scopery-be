package com.company.scopery.modules.aiagent.tool.application.response;

import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermission;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AiToolDetailResponse(
        UUID id,
        String code,
        String name,
        String description,
        String category,
        String mutationType,
        boolean requiresHumanApproval,
        String status,
        List<AiToolPermissionResponse> permissions,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiToolDetailResponse from(AiTool tool, List<AiToolPermission> permissions) {
        return new AiToolDetailResponse(
                tool.id(),
                tool.code().value(),
                tool.name(),
                tool.description(),
                tool.category(),
                tool.mutationType().name(),
                tool.requiresHumanApproval(),
                tool.status().name(),
                permissions.stream().map(AiToolPermissionResponse::from).toList(),
                tool.createdAt(),
                tool.updatedAt()
        );
    }
}
