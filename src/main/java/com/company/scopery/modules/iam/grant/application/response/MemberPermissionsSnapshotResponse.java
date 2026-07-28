package com.company.scopery.modules.iam.grant.application.response;

import java.util.List;
import java.util.UUID;

public record MemberPermissionsSnapshotResponse(
        UUID userId,
        List<UUID> permissionActionIds
) {}
