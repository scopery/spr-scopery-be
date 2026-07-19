package com.company.scopery.modules.resourcecapacity.shared.authorization;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.CreateWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.CreateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CapacityAuthorizationActionTest {

    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CalendarDayRuleRepository dayRuleRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private CapacityActivityLogger activityLogger;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityPlatformPublisher platformPublisher;

    private CreateWorkingCalendarAction createWorkingCalendarAction;

    private final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createWorkingCalendarAction = new CreateWorkingCalendarAction(
                workingCalendarRepository, dayRuleRepository, workspaceRepository,
                activityLogger, authorizationService, platformPublisher);
    }

    @Test
    void createCalendar_withoutPermission_forbidden() {
        doThrow(CapacityExceptions.accessDenied()).when(authorizationService).requireCalendarCreate(any());

        assertThatThrownBy(() -> createWorkingCalendarAction.execute(
                new CreateWorkingCalendarCommand(workspaceId, "CAL_01", "Calendar", null, "UTC", false)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(CapacityErrorCatalog.CAPACITY_ACCESS_DENIED.code());
                });
    }
}
