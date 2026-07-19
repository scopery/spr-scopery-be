package com.company.scopery.modules.collaboration.note.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.note.application.response.MeetingNoteResponse;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNoteRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.ArchiveNoteCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveNoteAction {
    private final MeetingNoteRepository notes; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public ArchiveNoteAction(MeetingNoteRepository notes, CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.notes=notes; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingNoteResponse execute(ArchiveNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var n = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        n = notes.save(n.archive(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, n.id(), CollaborationActivityActions.NOTE_ARCHIVED, "Note archived");
        return MeetingNoteResponse.from(n);
    }
}
