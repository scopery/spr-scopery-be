package com.company.scopery.modules.workspace.organization.application.query;

import java.util.UUID;

public record SearchOrganizationQuery(
        String keyword,
        UUID ownerUserId,
        String status,
        int page,
        int size) {
}
