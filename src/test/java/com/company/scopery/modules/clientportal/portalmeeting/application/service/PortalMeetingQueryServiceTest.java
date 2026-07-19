package com.company.scopery.modules.clientportal.portalmeeting.application.service;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.common.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortalMeetingQueryServiceTest {
    @Mock MeetingRepository meetings;
    @Mock MeetingMinutesRepository minutes;
    @Mock PortalGrantEnforcementService grantEnforcement;
    @InjectMocks PortalMeetingQueryService service;

    @Test
    void list_returnsOnlyClientVisibleNonArchived() {
        UUID projectId = UUID.randomUUID();
        Meeting visible = Meeting.create(UUID.randomUUID(), projectId, null, "Client", null,
                MeetingType.CLIENT_STATUS, Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), true);
        Meeting hidden = Meeting.create(UUID.randomUUID(), projectId, null, "Internal", null,
                MeetingType.GENERAL, Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), false);
        when(meetings.findByProjectId(projectId)).thenReturn(List.of(visible, hidden));

        var result = service.list(projectId);
        verify(grantEnforcement).requireActiveGrant(projectId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Client");
    }

    @Test
    void get_deniesInternalMeeting() {
        UUID projectId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        Meeting hidden = Meeting.create(UUID.randomUUID(), projectId, null, "Internal", null,
                MeetingType.GENERAL, Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), false);
        // recreate with known id via reflection-free path: mock find returns meeting with clientVisible false
        when(meetings.findByIdAndProjectId(meetingId, projectId)).thenReturn(Optional.of(hidden));
        assertThatThrownBy(() -> service.get(projectId, meetingId)).isInstanceOf(AppException.class);
    }

    @Test
    void listMinutes_requiresClientVisibleSummary() {
        UUID projectId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        Meeting visible = Meeting.create(UUID.randomUUID(), projectId, null, "Client", null,
                MeetingType.CLIENT_STATUS, Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), true);
        when(meetings.findByIdAndProjectId(meetingId, projectId)).thenReturn(Optional.of(visible));
        MeetingMinutes withClient = MeetingMinutes.create(visible.workspaceId(), projectId, meetingId, "full", null, null, "client ok");
        MeetingMinutes internalOnly = MeetingMinutes.create(visible.workspaceId(), projectId, meetingId, "full", null, null, null);
        when(minutes.findByMeetingId(meetingId)).thenReturn(List.of(withClient, internalOnly));

        var result = service.listMinutes(projectId, meetingId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).clientVisibleSummary()).isEqualTo("client ok");
    }
}
