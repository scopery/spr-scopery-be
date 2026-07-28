package com.company.scopery.modules.workspace.member.application.response;

import java.util.List;
import java.util.UUID;

public record WorkspaceMemberAccessResponse(
        UUID workspaceId,
        UUID userId,
        String accessMode,
        int totalProjects,
        List<ProjectAccessItem> projects,
        List<ProjectAccessItem> availableProjects
) {
    public record ProjectAccessItem(
            UUID projectId,
            String projectName,
            String projectCode
    ) {}
}
