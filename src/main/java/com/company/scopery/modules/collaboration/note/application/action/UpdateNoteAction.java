package com.company.scopery.modules.collaboration.note.application.action;
import com.company.scopery.modules.collaboration.note.application.response.MeetingNoteResponse;
import com.company.scopery.modules.collaboration.note.domain.enums.NoteType;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNoteRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.UpdateNoteCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateNoteAction {
    private final MeetingNoteRepository notes; private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public UpdateNoteAction(MeetingNoteRepository notes, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.notes=notes; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingNoteResponse execute(UpdateNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        var n = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        var type = CollaborationEnumParser.parseRequired(NoteType.class, c.noteType(), "noteType");
        n = notes.save(n.update(type, c.body(), Boolean.TRUE.equals(c.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, n.id(), CollaborationActivityActions.NOTE_UPDATED, "Note updated");
        return MeetingNoteResponse.from(n);
    }
}
