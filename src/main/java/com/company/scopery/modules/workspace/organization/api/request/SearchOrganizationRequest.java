package com.company.scopery.modules.workspace.organization.api.request;

import java.util.UUID;

public record SearchOrganizationRequest(
        String keyword,
        UUID ownerUserId,
        String status,
        int page,
        int size) {
}
