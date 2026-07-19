package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.query;

import java.util.UUID;

public record SearchUserCapacityProfileQuery(
        UUID workspaceId,
        UUID workspaceMemberId,
        UUID userId,
        String status,
        int page,
        int size
) {}
