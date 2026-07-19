package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response;

import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserCapacityProfileResponse(
        UUID id,
        UUID workspaceId,
        UUID workspaceMemberId,
        UUID userId,
        UUID workingCalendarId,
        BigDecimal defaultDailyHours,
        BigDecimal focusFactor,
        String capacityStatus,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Instant archivedAt,
        UUID archivedBy,
        int version
) {

    public static UserCapacityProfileResponse from(UserCapacityProfile profile) {
        return new UserCapacityProfileResponse(
                profile.id(),
                profile.workspaceId(),
                profile.workspaceMemberId(),
                profile.userId(),
                profile.workingCalendarId(),
                profile.defaultDailyHours(),
                profile.focusFactor(),
                profile.capacityStatus().name(),
                profile.effectiveFrom(),
                profile.effectiveTo(),
                profile.archivedAt(),
                profile.archivedBy(),
                profile.version()
        );
    }
}
