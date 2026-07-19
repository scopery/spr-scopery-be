package com.company.scopery.modules.collaboration.meeting.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.collaboration.meeting.application.command.CreateMeetingCommand;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationErrorCatalog;
import com.company.scopery.common.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant; import java.util.Optional; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CreateMeetingActionTest {
    @Mock ProjectRepository projects; @Mock MeetingRepository meetings;
    @Mock CollaborationAuthorizationService authorization; @Mock CurrentUserAuthorizationService currentUser;
    @Mock CollaborationActivityLogger activityLogger;
    @InjectMocks CreateMeetingAction action;
    @Test void createsMeeting() {
        UUID projectId = UUID.randomUUID(); UUID workspaceId = UUID.randomUUID(); UUID userId = UUID.randomUUID();
        Project project = mock(Project.class);
        when(project.id()).thenReturn(projectId); when(project.workspaceId()).thenReturn(workspaceId); when(project.status()).thenReturn(ProjectStatus.ACTIVE);
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        when(currentUser.resolveCurrentUser()).thenReturn(IamUser.create(Username.of("alice"), EmailAddress.of("a@x.com"), "Alice"));
        when(meetings.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var resp = action.execute(new CreateMeetingCommand(projectId, null, "Standup", null, "GENERAL", Instant.now(), null, null, null, null, null, false));
        assertThat(resp.title()).isEqualTo("Standup");
        verify(authorization).requireMeetingCreate(projectId);
        verify(activityLogger).logSuccess(anyString(), any(), anyString(), anyString());
    }
    @Test void blankTitleFails() {
        UUID projectId = UUID.randomUUID();
        Project project = mock(Project.class);
        when(project.status()).thenReturn(ProjectStatus.ACTIVE);
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        assertThatThrownBy(() -> action.execute(new CreateMeetingCommand(projectId, null, "  ", null, "GENERAL", Instant.now(), null, null, null, null, null, false)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode()).isEqualTo(CollaborationErrorCatalog.COLLABORATION_TITLE_REQUIRED.code()));
    }
}
