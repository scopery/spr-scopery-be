package com.company.scopery.modules.workspace.orgmember.application.response;

import java.util.UUID;

public record OrgMembershipSummaryResponse(
        UUID organizationId,
        String organizationName,
        String membershipType,
        String status) {
}
