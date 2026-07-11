package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record AddIamGrantPermissionActionCommand(
        UUID grantId,
        UUID permissionActionId,
        String permissionCode,
        String actionCode) {
}
