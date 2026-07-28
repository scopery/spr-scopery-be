package com.company.scopery.modules.iam.grant.application.command;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;

import java.util.UUID;

public record BootstrapProjectAccessCommand(
        UUID projectId,
        String projectName,
        UUID workspaceId,
        UUID organizationId,
        UUID ownerUserId
) {
    public IamResourceType resourceType() {
        return IamResourceType.PROJECT;
    }
}
