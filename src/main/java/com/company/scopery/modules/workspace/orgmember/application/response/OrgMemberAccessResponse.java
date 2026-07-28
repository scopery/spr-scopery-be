package com.company.scopery.modules.workspace.orgmember.application.response;

import java.util.List;
import java.util.UUID;

/** Hierarchy access map for one person inside an organization. */
public record OrgMemberAccessResponse(
        UUID organizationId,
        UUID userId,
        String orgMembershipStatus,
        List<WorkspaceMembershipAccessItem> workspaces
) {
    public record WorkspaceMembershipAccessItem(
            UUID workspaceId,
            String workspaceName,
            String membershipStatus,
            String accessMode,
            int totalProjects,
            List<ProjectAccessItem> projects,
            List<ProjectAccessItem> availableProjects
    ) {}

    public record ProjectAccessItem(
            UUID projectId,
            String projectName,
            String projectCode
    ) {}
}
