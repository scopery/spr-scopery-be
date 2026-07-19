package com.company.scopery.modules.resourcecapacity.workingcalendar.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWorkingCalendarRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotBlank String timezone,
        Boolean isDefault
) {}
