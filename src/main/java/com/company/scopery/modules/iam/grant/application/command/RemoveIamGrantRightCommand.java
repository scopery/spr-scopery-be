package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record RemoveIamGrantRightCommand(UUID grantId, UUID rightId) {
}
