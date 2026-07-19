package com.company.scopery.modules.resourcecapacity.workingcalendar.application.response;

import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;

import java.time.Instant;
import java.util.UUID;

public record WorkingCalendarResponse(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String description,
        String timezone,
        boolean isDefault,
        String status,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static WorkingCalendarResponse from(WorkingCalendar calendar) {
        return new WorkingCalendarResponse(
                calendar.id(),
                calendar.workspaceId(),
                calendar.code(),
                calendar.name(),
                calendar.description(),
                calendar.timezone(),
                calendar.isDefault(),
                calendar.status().name(),
                calendar.archivedAt(),
                calendar.archivedBy(),
                calendar.version(),
                calendar.createdAt(),
                calendar.updatedAt()
        );
    }
}
