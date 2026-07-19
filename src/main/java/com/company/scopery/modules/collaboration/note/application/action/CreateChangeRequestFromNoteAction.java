package com.company.scopery.modules.collaboration.note.application.action;
import com.company.scopery.modules.collaboration.artifactlink.domain.enums.ArtifactLinkType;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLink;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLinkRepository;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.note.application.response.NoteConversionResponse;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNoteRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.projectbaseline.changerequest.application.action.CreateChangeRequestAction;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.CreateChangeRequestFromNoteCommand;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateChangeRequestFromNoteAction {
    private final MeetingRepository meetings;
    private final MeetingNoteRepository notes;
    private final MeetingArtifactLinkRepository links;
    private final CreateChangeRequestAction createChangeRequest;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;

    public CreateChangeRequestFromNoteAction(MeetingRepository meetings, MeetingNoteRepository notes,
                                             MeetingArtifactLinkRepository links,
                                             CreateChangeRequestAction createChangeRequest,
                                             CollaborationAuthorizationService authorization,
                                             CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.notes = notes; this.links = links;
        this.createChangeRequest = createChangeRequest; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public NoteConversionResponse execute(CreateChangeRequestFromNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var note = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        if (note.archivedAt() != null) throw CollaborationExceptions.noteArchived(c.noteId());
        String t = (c.title() == null || c.title().isBlank()) ? truncate(note.body(), 200) : c.title().trim();
        String d = (c.description() == null || c.description().isBlank()) ? note.body() : c.description().trim();
        String ct = (c.changeType() == null || c.changeType().isBlank()) ? "SCOPE_CHANGE" : c.changeType().trim();
        String r = (c.reason() == null || c.reason().isBlank()) ? "Created from meeting note" : c.reason().trim();
        var cr = createChangeRequest.execute(new CreateChangeRequestCommand(
                c.projectId(), c.code(), t, d, ct, c.priority(), c.baselineId(), r));
        if (links.existsActive(c.meetingId(), "CHANGE_REQUEST", cr.id())) throw CollaborationExceptions.linkDuplicate();
        var link = links.save(MeetingArtifactLink.create(
                note.workspaceId(), note.projectId(), note.meetingId(), note.agendaItemId(), note.id(), null,
                "CHANGE_REQUEST", cr.id(), ArtifactLinkType.FOLLOW_UP_FOR));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, note.id(),
                CollaborationActivityActions.NOTE_CONVERTED_CHANGE_REQUEST, "Note converted to CR " + cr.id());
        return new NoteConversionResponse(note.id(), c.meetingId(), "CHANGE_REQUEST", cr.id(), link.id());
    }

    private static String truncate(String s, int max) {
        if (s == null || s.isBlank()) return "Change request from meeting note";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
