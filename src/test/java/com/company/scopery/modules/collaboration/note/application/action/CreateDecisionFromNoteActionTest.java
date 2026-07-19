package com.company.scopery.modules.collaboration.note.application.action;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLinkRepository;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.note.domain.enums.NoteType;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNote;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNoteRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.raid.decision.application.action.CreateDecisionAction;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDecisionFromNoteActionTest {
    @Mock MeetingRepository meetings;
    @Mock MeetingNoteRepository notes;
    @Mock MeetingArtifactLinkRepository links;
    @Mock CreateDecisionAction createDecision;
    @Mock CollaborationAuthorizationService authorization;
    @Mock CollaborationActivityLogger activityLogger;
    @InjectMocks CreateDecisionFromNoteAction action;

    @Test
    void execute_createsDecisionAndLink() {
        UUID projectId = UUID.randomUUID();
        Meeting meeting = Meeting.create(UUID.randomUUID(), projectId, null, "M", null,
                MeetingType.DECISION_MEETING, Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), false);
        MeetingNote note = MeetingNote.create(meeting.workspaceId(), projectId, meeting.id(), null, NoteType.DECISION_NOTE, "We will use Postgres", false);
        when(meetings.findByIdAndProjectId(meeting.id(), projectId)).thenReturn(Optional.of(meeting));
        when(notes.findByIdAndMeetingId(note.id(), meeting.id())).thenReturn(Optional.of(note));
        UUID decisionId = UUID.randomUUID();
        when(createDecision.execute(any())).thenReturn(new DecisionRecordResponse(
                decisionId, projectId, "D1", "title", "OTHER", "DRAFT", "rationale", null, null, null, Instant.now()));
        when(links.existsActive(meeting.id(), "DECISION", decisionId)).thenReturn(false);
        when(links.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = action.execute(new com.company.scopery.modules.collaboration.note.application.command.CreateDecisionFromNoteCommand(
                projectId, meeting.id(), note.id(), null, null, null, "D1"));
        assertThat(result.targetType()).isEqualTo("DECISION");
        assertThat(result.targetId()).isEqualTo(decisionId);
        assertThat(result.artifactLinkId()).isNotNull();
        verify(createDecision).execute(any());
        verify(links).save(any());
        verify(activityLogger).logSuccess(anyString(), eq(note.id()), anyString(), anyString());
    }
}
