package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.CreateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateUserCapacityProfileAction {

    private final UserCapacityProfileRepository profileRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public CreateUserCapacityProfileAction(UserCapacityProfileRepository profileRepository,
                                           WorkspaceMemberRepository workspaceMemberRepository,
                                           WorkingCalendarRepository workingCalendarRepository,
                                           CapacityActivityLogger activityLogger,
                                           CapacityWorkspaceAuthorizationService authorizationService,
                                           CapacityPlatformPublisher platformPublisher) {
        this.profileRepository = profileRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public UserCapacityProfileResponse execute(CreateUserCapacityProfileCommand cmd) {
        authorizationService.requireProfileCreate(cmd.workspaceId());

        WorkspaceMember member = workspaceMemberRepository.findById(cmd.workspaceMemberId())
                .orElseThrow(() -> CapacityExceptions.profileMemberNotFound(cmd.workspaceMemberId()));
        if (!member.workspaceId().equals(cmd.workspaceId())) {
            throw CapacityExceptions.profileMemberNotFound(cmd.workspaceMemberId());
        }
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw CapacityExceptions.profileMemberInactive(cmd.workspaceMemberId());
        }

        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.workingCalendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.workingCalendarId()));
        if (!calendar.workspaceId().equals(cmd.workspaceId())) {
            throw CapacityExceptions.profileCalendarWorkspaceMismatch(cmd.workingCalendarId(), cmd.workspaceId());
        }
        if (calendar.status() != WorkingCalendarStatus.ACTIVE) {
            throw CapacityExceptions.calendarNotActive(cmd.workingCalendarId());
        }

        validateHours(cmd.defaultDailyHours());
        validateFocusFactor(cmd.focusFactor());
        validateDateRange(cmd.effectiveFrom(), cmd.effectiveTo());

        for (UserCapacityProfile existing : profileRepository.findActiveByWorkspaceMemberId(cmd.workspaceMemberId())) {
            if (existing.overlaps(cmd.effectiveFrom(), cmd.effectiveTo())) {
                throw CapacityExceptions.profileOverlap(cmd.workspaceMemberId());
            }
        }

        UserCapacityProfile profile = UserCapacityProfile.create(
                cmd.workspaceId(), cmd.workspaceMemberId(), member.userId(), cmd.workingCalendarId(),
                cmd.defaultDailyHours(), cmd.focusFactor(), cmd.effectiveFrom(), cmd.effectiveTo());
        UserCapacityProfile saved = profileRepository.save(profile);

        platformPublisher.enqueueProfile(saved, "CAPACITY_PROFILE_CREATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.USER_CAPACITY_PROFILE,
                saved.id(),
                CapacityActivityActions.CAPACITY_PROFILE_CREATED,
                "Capacity profile created for member: " + saved.workspaceMemberId());

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
