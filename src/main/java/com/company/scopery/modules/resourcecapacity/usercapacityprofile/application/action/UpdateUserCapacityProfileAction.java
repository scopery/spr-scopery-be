package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.UpdateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class UpdateUserCapacityProfileAction {

    private final UserCapacityProfileRepository profileRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public UpdateUserCapacityProfileAction(UserCapacityProfileRepository profileRepository,
                                           WorkingCalendarRepository workingCalendarRepository,
                                           CapacityActivityLogger activityLogger,
                                           CapacityWorkspaceAuthorizationService authorizationService,
                                           CapacityPlatformPublisher platformPublisher) {
        this.profileRepository = profileRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public UserCapacityProfileResponse execute(UpdateUserCapacityProfileCommand cmd) {
        UserCapacityProfile profile = profileRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.profileNotFound(cmd.id()));
        authorizationService.requireProfileUpdate(profile.workspaceId());

        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.workingCalendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.workingCalendarId()));
        if (!calendar.workspaceId().equals(profile.workspaceId())) {
            throw CapacityExceptions.profileCalendarWorkspaceMismatch(cmd.workingCalendarId(), profile.workspaceId());
        }
        if (calendar.status() != WorkingCalendarStatus.ACTIVE) {
            throw CapacityExceptions.calendarNotActive(cmd.workingCalendarId());
        }

        validateHours(cmd.defaultDailyHours());
        validateFocusFactor(cmd.focusFactor());
        validateDateRange(cmd.effectiveFrom(), cmd.effectiveTo());

        boolean focusFactorChanged = profile.focusFactor().compareTo(cmd.focusFactor()) != 0;

        for (UserCapacityProfile existing : profileRepository.findActiveByWorkspaceMemberId(profile.workspaceMemberId())) {
            if (!existing.id().equals(profile.id()) && existing.overlaps(cmd.effectiveFrom(), cmd.effectiveTo())) {
                throw CapacityExceptions.profileOverlap(profile.workspaceMemberId());
            }
        }

        UserCapacityProfile updated = profile.update(
                cmd.workingCalendarId(), cmd.defaultDailyHours(), cmd.focusFactor(),
                cmd.effectiveFrom(), cmd.effectiveTo());
        UserCapacityProfile saved = profileRepository.save(updated);

        platformPublisher.enqueueProfile(saved, "CAPACITY_PROFILE_UPDATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.USER_CAPACITY_PROFILE,
                saved.id(),
                CapacityActivityActions.CAPACITY_PROFILE_UPDATED,
                "Capacity profile updated: " + saved.id());

        if (focusFactorChanged) {
            platformPublisher.audit(
                    AuditEventType.CAPACITY_FOCUS_FACTOR_CHANGED,
                    null, CapacityEntityTypes.USER_CAPACITY_PROFILE, saved.id(),
                    null, saved.workspaceId(), java.util.Map.of("focusFactor", saved.focusFactor()),
                    "Focus factor changed for profile: " + saved.id());
        }

        return UserCapacityProfileResponse.from(saved);
    }

    private void validateHours(BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) <= 0 || hours.compareTo(new BigDecimal("24")) > 0) {
            throw CapacityExceptions.profileInvalidDailyHours(hours);
        }
    }

    private void validateFocusFactor(BigDecimal focusFactor) {
        if (focusFactor == null || focusFactor.compareTo(BigDecimal.ZERO) <= 0
                || focusFactor.compareTo(BigDecimal.ONE) > 0) {
            throw CapacityExceptions.profileInvalidFocusFactor(focusFactor);
        }
    }

    private void validateDateRange(java.time.LocalDate from, java.time.LocalDate to) {
        if (from == null || (to != null && to.isBefore(from))) {
            throw CapacityExceptions.profileDateRangeInvalid();
        }
    }
}
