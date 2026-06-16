package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record CreateIamAccessGrantCommand(
        String subjectType,
        UUID subjectId,
        UUID resourceId,
        UUID roleId,
        String effect,
        String scopeType,
        UUID scopeRefId,
        UUID workspaceId,
        UUID grantedBy) {
}
