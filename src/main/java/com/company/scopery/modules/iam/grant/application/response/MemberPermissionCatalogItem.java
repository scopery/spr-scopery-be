package com.company.scopery.modules.iam.grant.application.response;

import java.util.List;
import java.util.UUID;

/** Owner-facing permission row — title/description only (no right codes for UI). */
public record MemberPermissionCatalogItem(
        UUID permissionActionId,
        String title,
        String description,
        String module,
        List<UUID> grantedUserIds,
        boolean baseline
) {}
