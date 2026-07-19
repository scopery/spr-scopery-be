package com.company.scopery.modules.resourcecapacity.workingcalendar.application.command;

import java.util.UUID;

public record CreateWorkingCalendarCommand(
        UUID workspaceId,
        String code,
        String name,
        String description,
        String timezone,
        Boolean isDefault
) {}
