package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record RemoveIamGrantPermissionActionCommand(UUID grantId, UUID permissionActionId) {
}
