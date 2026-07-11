package com.company.scopery.modules.workspace.orgmember.application.query;

import java.util.UUID;

public record SearchOrgMemberQuery(
        UUID organizationId,
        UUID userId,
        String status,
        int page,
        int size) {
}
