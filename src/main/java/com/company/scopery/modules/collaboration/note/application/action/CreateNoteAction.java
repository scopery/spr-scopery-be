package com.company.scopery.modules.collaboration.note.application.action;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.note.application.response.MeetingNoteResponse;
import com.company.scopery.modules.collaboration.note.domain.enums.NoteType;
import com.company.scopery.modules.collaboration.note.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.CreateNoteCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateNoteAction {
    private final MeetingRepository meetings; private final MeetingNoteRepository notes;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateNoteAction(MeetingRepository meetings, MeetingNoteRepository notes, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.notes=notes; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingNoteResponse execute(CreateNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var type = CollaborationEnumParser.parseRequired(NoteType.class, c.noteType(), "noteType");
        var n = MeetingNote.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), c.agendaItemId(), type, c.body(), Boolean.TRUE.equals(c.clientVisible()));
        n = notes.save(n);
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, n.id(), CollaborationActivityActions.NOTE_CREATED, "Note created");
        return MeetingNoteResponse.from(n);
    }
}
