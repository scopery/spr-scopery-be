package com.company.scopery.modules.workspace.member.application.response;

import java.util.List;
import java.util.UUID;

/**
 * Inferred project-access snapshot for one workspace member (no DB access-mode column).
 */
public record MemberProjectAccessSnapshot(
        UUID workspaceId,
        UUID userId,
        String accessMode,
        int totalProjects,
        List<ProjectAccessItem> projects,
        List<ProjectAccessItem> availableProjects
) {
    public static final String MODE_ALL = "ALL";
    public static final String MODE_CUSTOM = "CUSTOM";

    public boolean isFullAccess() {
        return MODE_ALL.equals(accessMode);
    }

    public record ProjectAccessItem(
            UUID projectId,
            String projectName,
            String projectCode
    ) {}
}
