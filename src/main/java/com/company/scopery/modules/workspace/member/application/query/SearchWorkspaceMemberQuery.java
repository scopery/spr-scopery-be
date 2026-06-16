package com.company.scopery.modules.workspace.member.application.query;

import java.util.UUID;

public record SearchWorkspaceMemberQuery(
        UUID workspaceId,
        UUID userId,
        String status,
        int page,
        int size) {
}
