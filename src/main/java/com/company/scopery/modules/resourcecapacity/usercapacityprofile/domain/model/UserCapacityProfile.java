package com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model;

import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.enums.UserCapacityProfileStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserCapacityProfile(
        UUID id,
        UUID workspaceId,
        UUID workspaceMemberId,
        UUID userId,
        UUID workingCalendarId,
        BigDecimal defaultDailyHours,
        BigDecimal focusFactor,
        UserCapacityProfileStatus capacityStatus,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserCapacityProfile create(
            UUID workspaceId,
            UUID workspaceMemberId,
            UUID userId,
            UUID workingCalendarId,
            BigDecimal defaultDailyHours,
            BigDecimal focusFactor,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        return new UserCapacityProfile(
                UUID.randomUUID(), workspaceId, workspaceMemberId, userId, workingCalendarId,
                defaultDailyHours, focusFactor, UserCapacityProfileStatus.ACTIVE,
                effectiveFrom, effectiveTo, null, null, 0, null, null
        );
    }

    public UserCapacityProfile update(
            UUID workingCalendarId,
            BigDecimal defaultDailyHours,
            BigDecimal focusFactor,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        return new UserCapacityProfile(
                this.id, this.workspaceId, this.workspaceMemberId, this.userId, workingCalendarId,
                defaultDailyHours, focusFactor, this.capacityStatus,
                effectiveFrom, effectiveTo, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public UserCapacityProfile activate() {
        return withStatus(UserCapacityProfileStatus.ACTIVE);
    }

    public UserCapacityProfile deactivate() {
        return withStatus(UserCapacityProfileStatus.INACTIVE);
    }

    public UserCapacityProfile archive(UUID actorId) {
        return new UserCapacityProfile(
                this.id, this.workspaceId, this.workspaceMemberId, this.userId, this.workingCalendarId,
                this.defaultDailyHours, this.focusFactor, UserCapacityProfileStatus.ARCHIVED,
                this.effectiveFrom, this.effectiveTo, Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }

    private UserCapacityProfile withStatus(UserCapacityProfileStatus status) {
        return new UserCapacityProfile(
                this.id, this.workspaceId, this.workspaceMemberId, this.userId, this.workingCalendarId,
                this.defaultDailyHours, this.focusFactor, status,
                this.effectiveFrom, this.effectiveTo, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public boolean overlaps(LocalDate otherFrom, LocalDate otherTo) {
        LocalDate thisEnd = this.effectiveTo != null ? this.effectiveTo : LocalDate.MAX;
        LocalDate otherEnd = otherTo != null ? otherTo : LocalDate.MAX;
        return !this.effectiveFrom.isAfter(otherEnd) && !otherFrom.isAfter(thisEnd);
    }
}
