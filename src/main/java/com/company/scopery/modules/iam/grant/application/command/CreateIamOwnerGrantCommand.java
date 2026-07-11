package com.company.scopery.modules.iam.grant.application.command;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;

import java.util.UUID;

public record CreateIamOwnerGrantCommand(
        UUID resourceId,
        UUID ownerUserId,
        IamResourceType resourceType,
        String entityType,
        UUID entityId) {
}
