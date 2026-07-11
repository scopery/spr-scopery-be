package com.company.scopery.modules.workspace.orgmember.application.command;

import java.util.UUID;

public record AddOrgMemberCommand(
        UUID organizationId,
        UUID userId,
        String membershipType) {
}
