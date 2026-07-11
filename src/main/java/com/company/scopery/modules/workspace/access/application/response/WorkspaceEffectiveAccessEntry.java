package com.company.scopery.modules.workspace.access.application.response;

import java.util.List;
import java.util.UUID;

public record WorkspaceEffectiveAccessEntry(
        UUID workspaceId,
        String workspaceCode,
        String workspaceName,
        List<String> accessSources,
        List<UUID> contributingGrantIds) {
}
