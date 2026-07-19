package com.company.scopery.modules.resourcecapacity.workingcalendar.http.request;

public record UpdateWorkingCalendarRequest(
        String name,
        String description,
        String timezone
) {}
