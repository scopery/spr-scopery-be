package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.ArchiveWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.CreateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.SetDefaultWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityCalendarBusinessRulesActionTest {

    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CalendarDayRuleRepository dayRuleRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private CapacityActivityLogger activityLogger;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityPlatformPublisher platformPublisher;
    @Mock private CurrentUserAuthorizationService currentUserAuthorizationService;

    private CreateWorkingCalendarAction createWorkingCalendarAction;
    private ArchiveWorkingCalendarAction archiveWorkingCalendarAction;
    private SetDefaultWorkingCalendarAction setDefaultWorkingCalendarAction;

    private final UUID organizationId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createWorkingCalendarAction = new CreateWorkingCalendarAction(
                workingCalendarRepository, dayRuleRepository, workspaceRepository,
                activityLogger, authorizationService, platformPublisher);
        archiveWorkingCalendarAction = new ArchiveWorkingCalendarAction(
                workingCalendarRepository, activityLogger, authorizationService,
                platformPublisher, currentUserAuthorizationService);
        setDefaultWorkingCalendarAction = new SetDefaultWorkingCalendarAction(
                workingCalendarRepository, activityLogger, authorizationService, platformPublisher);

        Instant now = Instant.now();
        IamUser currentUser = IamUser.of(UUID.randomUUID(), Username.of("actor"),
                EmailAddress.of("actor@example.com"), "Actor", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserAuthorizationService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createCalendar_valid_success() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace(WorkspaceStatus.ACTIVE)));
        when(workingCalendarRepository.existsByWorkspaceIdAndCode(workspaceId, "CAL_01")).thenReturn(false);
        when(workingCalendarRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = createWorkingCalendarAction.execute(
                new CreateWorkingCalendarCommand(workspaceId, "CAL_01", "Calendar", null, "UTC", false));

        assertThat(response.code()).isEqualTo("CAL_01");
        assertThat(response.status()).isEqualTo(WorkingCalendarStatus.ACTIVE.name());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CalendarDayRule>> rulesCaptor = ArgumentCaptor.forClass(List.class);
        verify(dayRuleRepository).replaceAll(any(UUID.class), rulesCaptor.capture());
        assertThat(rulesCaptor.getValue()).hasSize(7);
        assertThat(rulesCaptor.getValue()).filteredOn(CalendarDayRule::isWorkingDay).hasSize(5);
    }

    @Test
    void createCalendar_duplicateCodeSameWorkspace_conflict() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace(WorkspaceStatus.ACTIVE)));
        when(workingCalendarRepository.existsByWorkspaceIdAndCode(workspaceId, "CAL_01")).thenReturn(true);

        assertThatThrownBy(() -> createWorkingCalendarAction.execute(
                new CreateWorkingCalendarCommand(workspaceId, "CAL_01", "Calendar", null, "UTC", false)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(CapacityErrorCatalog.CAPACITY_CALENDAR_CODE_ALREADY_EXISTS.code());
                });
    }

    @Test
    void createCalendar_invalidTimezone_rejected() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace(WorkspaceStatus.ACTIVE)));

        assertThatThrownBy(() -> createWorkingCalendarAction.execute(
                new CreateWorkingCalendarCommand(workspaceId, "CAL_01", "Calendar", null, "Not/AZone", false)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_CALENDAR_INVALID_TIMEZONE.code()));
    }

    @Test
    void createCalendar_workspaceNotActive_rejected() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace(WorkspaceStatus.ARCHIVED)));

        assertThatThrownBy(() -> createWorkingCalendarAction.execute(
                new CreateWorkingCalendarCommand(workspaceId, "CAL_01", "Calendar", null, "UTC", false)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_CALENDAR_WORKSPACE_NOT_ACTIVE.code()));
    }

    @Test
    void setDefaultCalendar_valid_success() {
        WorkingCalendar calendar = calendar(calendarId, workspaceId, false, WorkingCalendarStatus.ACTIVE);
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar));
        when(workingCalendarRepository.findAllActiveDefaultsByWorkspaceId(workspaceId)).thenReturn(List.of());
        when(workingCalendarRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = setDefaultWorkingCalendarAction.execute(new SetDefaultWorkingCalendarCommand(calendarId));

        assertThat(response.isDefault()).isTrue();
        verify(platformPublisher).enqueueCalendar(any(), eq("CAPACITY_CALENDAR_DEFAULT_CHANGED"));
    }

    @Test
    void archiveCalendar_assignedToProfile_rejected() {
        WorkingCalendar calendar = calendar(calendarId, workspaceId, false, WorkingCalendarStatus.ACTIVE);
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar));
        when(workingCalendarRepository.isReferencedByCapacityProfiles(calendarId)).thenReturn(true);

        assertThatThrownBy(() -> archiveWorkingCalendarAction.execute(new ArchiveWorkingCalendarCommand(calendarId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_CALENDAR_IN_USE.code()));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Workspace workspace(WorkspaceStatus status) {
        Instant now = Instant.now();
        return new Workspace(workspaceId, organizationId, WorkspaceCode.of("WS_01"), "Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY,
                status, 0, now, now);
    }

    private WorkingCalendar calendar(UUID id, UUID ownerWorkspaceId, boolean isDefault, WorkingCalendarStatus status) {
        Instant now = Instant.now();
        return new WorkingCalendar(id, ownerWorkspaceId, "CAL_01", "Calendar", null, "UTC",
                isDefault, status, null, null, 0, now, now);
    }
}
