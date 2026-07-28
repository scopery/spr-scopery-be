package com.company.scopery.modules.iam.grant.application.response;

import java.util.List;
import java.util.UUID;

public record MemberPermissionCatalogResponse(
        String resourceType,
        UUID resourceRefId,
        List<MemberPermissionCatalogItem> items
) {}
