package com.company.scopery.modules.workspace.workspace.api.request;

import java.util.UUID;

public record SearchWorkspaceRequest(
        UUID organizationId,
        UUID ownerUserId,
        String keyword,
        String status,
        int page,
        int size) {
}
