package com.company.scopery.modules.workspace.workspace.application.response;

import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceDetailResponse(
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
        Instant updatedAt,
        boolean ownerMembershipCreated) {

    public static WorkspaceDetailResponse from(Workspace ws, boolean ownerMembershipCreated) {
        return new WorkspaceDetailResponse(
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
                ws.updatedAt(),
                ownerMembershipCreated);
    }
}
