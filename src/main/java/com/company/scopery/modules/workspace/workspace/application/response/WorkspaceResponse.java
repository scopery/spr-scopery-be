package com.company.scopery.modules.workspace.workspace.application.response;

import com.company.scopery.modules.workspace.workspace.domain.Workspace;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceResponse(
        UUID id,
        UUID organizationId,
        String code,
        String name,
        String description,
        UUID ownerUserId,
        String defaultVisibility,
        String joinPolicy,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceResponse from(Workspace ws) {
        return new WorkspaceResponse(
                ws.id(),
                ws.organizationId(),
                ws.code().value(),
                ws.name(),
                ws.description(),
                ws.ownerUserId(),
                ws.defaultVisibility().name(),
                ws.joinPolicy() != null ? ws.joinPolicy().name() : "INVITE_ONLY",
                ws.status().name(),
                ws.createdAt(),
                ws.updatedAt());
    }
}
