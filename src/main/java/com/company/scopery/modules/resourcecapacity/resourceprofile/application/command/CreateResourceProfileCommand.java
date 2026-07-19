package com.company.scopery.modules.resourcecapacity.resourceprofile.application.command;
import java.util.UUID;
public record CreateResourceProfileCommand(UUID workspaceId, String resourceType, String displayName,
        UUID linkedUserId, UUID linkedWorkspaceMemberId, UUID primaryRoleId) {}
