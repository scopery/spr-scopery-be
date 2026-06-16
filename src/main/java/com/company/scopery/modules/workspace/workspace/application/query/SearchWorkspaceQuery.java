package com.company.scopery.modules.workspace.workspace.application.query;

import java.util.UUID;

public record SearchWorkspaceQuery(
        UUID organizationId,
        UUID ownerUserId,
        String keyword,
        String status,
        int page,
        int size) {
}
