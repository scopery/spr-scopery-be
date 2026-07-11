package com.company.scopery.modules.workspace.orgmember.application.command;

import java.util.UUID;

public record ChangeOrgMemberStatusCommand(UUID organizationId, UUID memberId) {
}
