package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.CreateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityProfileBusinessRulesActionTest {

    @Mock private UserCapacityProfileRepository profileRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CapacityActivityLogger activityLogger;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityPlatformPublisher platformPublisher;

    private CreateUserCapacityProfileAction createUserCapacityProfileAction;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID otherWorkspaceId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createUserCapacityProfileAction = new CreateUserCapacityProfileAction(
                profileRepository, workspaceMemberRepository, workingCalendarRepository,
                activityLogger, authorizationService, platformPublisher);
    }

    @Test
    void createProfile_valid_success() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar(workspaceId)));
        when(profileRepository.findActiveByWorkspaceMemberId(memberId)).thenReturn(List.of());
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), new BigDecimal("0.8"),
                LocalDate.of(2026, 1, 1), null));

        assertThat(response.capacityStatus()).isEqualTo("ACTIVE");
        assertThat(response.focusFactor()).isEqualByComparingTo(new BigDecimal("0.8"));
    }

    @Test
    void createProfile_inactiveWorkspaceMember_rejected() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.INACTIVE)));

        assertThatThrownBy(() -> createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), new BigDecimal("0.8"),
                LocalDate.of(2026, 1, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_PROFILE_MEMBER_INACTIVE.code()));
    }

    @Test
    void createProfile_calendarDifferentWorkspace_rejected() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar(otherWorkspaceId)));

        assertThatThrownBy(() -> createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), new BigDecimal("0.8"),
                LocalDate.of(2026, 1, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(CapacityErrorCatalog.CAPACITY_PROFILE_CALENDAR_WORKSPACE_MISMATCH.code());
                });
    }

    @Test
    void createProfile_invalidFocusFactorZero_rejected() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar(workspaceId)));

        assertThatThrownBy(() -> createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_PROFILE_INVALID_FOCUS_FACTOR.code()));
    }

    @Test
    void createProfile_invalidFocusFactorAboveOne_rejected() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar(workspaceId)));

        assertThatThrownBy(() -> createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), new BigDecimal("1.5"),
                LocalDate.of(2026, 1, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_PROFILE_INVALID_FOCUS_FACTOR.code()));
    }

    @Test
    void createProfile_overlappingActiveProfile_rejected() {
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar(workspaceId)));

        UserCapacityProfile existing = UserCapacityProfile.create(
                workspaceId, memberId, userId, calendarId, new BigDecimal("8"), new BigDecimal("0.75"),
                LocalDate.of(2026, 1, 1), null);
        when(profileRepository.findActiveByWorkspaceMemberId(memberId)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> createUserCapacityProfileAction.execute(new CreateUserCapacityProfileCommand(
                workspaceId, memberId, calendarId, new BigDecimal("8"), new BigDecimal("0.8"),
                LocalDate.of(2026, 3, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(CapacityErrorCatalog.CAPACITY_PROFILE_OVERLAP.code());
                });
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private WorkspaceMember member(WorkspaceMemberStatus status) {
        Instant now = Instant.now();
        return new WorkspaceMember(memberId, workspaceId, userId, status, now, now, now);
    }

    private WorkingCalendar calendar(UUID ownerWorkspaceId) {
        Instant now = Instant.now();
        return new WorkingCalendar(calendarId, ownerWorkspaceId, "CAL_01", "Calendar", null, "UTC",
                false, WorkingCalendarStatus.ACTIVE, null, null, 0, now, now);
    }
}
