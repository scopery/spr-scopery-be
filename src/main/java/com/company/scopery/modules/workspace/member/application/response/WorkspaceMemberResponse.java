package com.company.scopery.modules.workspace.member.application.response;

import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceMemberResponse(
        UUID id,
        UUID workspaceId,
        UUID userId,
        String status,
        Instant joinedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceMemberResponse from(WorkspaceMember m) {
        return new WorkspaceMemberResponse(
                m.id(),
                m.workspaceId(),
                m.userId(),
                m.status().name(),
                m.joinedAt(),
                m.createdAt(),
                m.updatedAt());
    }
}
