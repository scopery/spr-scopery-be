package com.company.scopery.modules.aiagent.tool.application.response;

import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermission;

import java.time.Instant;
import java.util.UUID;

public record AiToolPermissionResponse(
        UUID id,
        UUID toolId,
        String permissionCode,
        String description,
        Instant createdAt
) {
    public static AiToolPermissionResponse from(AiToolPermission permission) {
        return new AiToolPermissionResponse(
                permission.id(),
                permission.toolId(),
                permission.permissionCode(),
                permission.description(),
                permission.createdAt()
        );
    }
}
