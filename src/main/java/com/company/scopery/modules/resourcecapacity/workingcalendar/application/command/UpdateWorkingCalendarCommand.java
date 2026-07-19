package com.company.scopery.modules.resourcecapacity.workingcalendar.application.command;

import java.util.UUID;

public record UpdateWorkingCalendarCommand(
        UUID id,
        String name,
        String description,
        String timezone
) {}
