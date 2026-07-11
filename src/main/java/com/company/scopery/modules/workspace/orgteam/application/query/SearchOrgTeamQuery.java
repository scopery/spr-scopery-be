package com.company.scopery.modules.workspace.orgteam.application.query;

import java.util.UUID;

public record SearchOrgTeamQuery(
        UUID organizationId,
        String keyword,
        String status,
        int page,
        int size) {
}
