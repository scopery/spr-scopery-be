package com.company.scopery.modules.resourcecapacity.workingcalendar.application.query;

import java.util.UUID;

public record SearchWorkingCalendarQuery(
        UUID workspaceId,
        String status,
        Boolean isDefault,
        String code,
        int page,
        int size
) {}
