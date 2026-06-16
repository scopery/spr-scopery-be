package com.company.scopery.modules.workspace.team.application.query;

import java.util.UUID;

public record SearchTeamQuery(
        UUID workspaceId,
        String status,
        int page,
        int size) {
}
