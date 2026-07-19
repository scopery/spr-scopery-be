package com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model;

import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;

import java.time.Instant;
import java.util.UUID;

public record WorkingCalendar(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String description,
        String timezone,
        boolean isDefault,
        WorkingCalendarStatus status,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static WorkingCalendar create(
            UUID workspaceId,
            String code,
            String name,
            String description,
            String timezone,
            boolean isDefault) {
        return new WorkingCalendar(
                UUID.randomUUID(),
                workspaceId,
                code,
                name,
                description,
                timezone,
                isDefault,
                WorkingCalendarStatus.ACTIVE,
                null,
                null,
                0,
                null,
                null
        );
    }

    public WorkingCalendar update(String name, String description, String timezone) {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, name, description, timezone,
                this.isDefault, this.status, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public WorkingCalendar activate() {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, this.name, this.description, this.timezone,
                this.isDefault, WorkingCalendarStatus.ACTIVE, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public WorkingCalendar deactivate() {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, this.name, this.description, this.timezone,
                this.isDefault, WorkingCalendarStatus.INACTIVE, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public WorkingCalendar archive(UUID actorId) {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, this.name, this.description, this.timezone,
                false, WorkingCalendarStatus.ARCHIVED, Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public WorkingCalendar setDefault() {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, this.name, this.description, this.timezone,
                true, this.status, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public WorkingCalendar clearDefault() {
        return new WorkingCalendar(
                this.id, this.workspaceId, this.code, this.name, this.description, this.timezone,
                false, this.status, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
