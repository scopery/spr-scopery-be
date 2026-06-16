package com.company.scopery.modules.workspace.member.api.request;

import java.util.UUID;

public record SearchWorkspaceMemberRequest(
        UUID userId,
        String status,
        int page,
        int size) {
}
