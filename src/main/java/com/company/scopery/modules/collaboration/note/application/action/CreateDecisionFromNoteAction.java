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
import com.company.scopery.modules.raid.decision.application.action.CreateDecisionAction;
import com.company.scopery.modules.raid.decision.application.command.CreateDecisionCommand;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.CreateDecisionFromNoteCommand;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDecisionFromNoteAction {
    private final MeetingRepository meetings;
    private final MeetingNoteRepository notes;
    private final MeetingArtifactLinkRepository links;
    private final CreateDecisionAction createDecision;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;

    public CreateDecisionFromNoteAction(MeetingRepository meetings, MeetingNoteRepository notes,
                                        MeetingArtifactLinkRepository links, CreateDecisionAction createDecision,
                                        CollaborationAuthorizationService authorization,
                                        CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.notes = notes; this.links = links;
        this.createDecision = createDecision; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public NoteConversionResponse execute(CreateDecisionFromNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var note = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        if (note.archivedAt() != null) throw CollaborationExceptions.noteArchived(c.noteId());
        String t = (c.title() == null || c.title().isBlank()) ? truncate(note.body(), 200) : c.title().trim();
        String r = (c.rationale() == null || c.rationale().isBlank()) ? note.body() : c.rationale().trim();
        String cat = (c.category() == null || c.category().isBlank()) ? "OTHER" : c.category().trim();
        var decision = createDecision.execute(new CreateDecisionCommand(c.projectId(), t, r, cat, c.code()));
        if (links.existsActive(c.meetingId(), "DECISION", decision.id())) throw CollaborationExceptions.linkDuplicate();
        var link = links.save(MeetingArtifactLink.create(
                note.workspaceId(), note.projectId(), note.meetingId(), note.agendaItemId(), note.id(), null,
                "DECISION", decision.id(), ArtifactLinkType.DECIDED));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, note.id(),
                CollaborationActivityActions.NOTE_CONVERTED_DECISION, "Note converted to decision " + decision.id());
        return new NoteConversionResponse(note.id(), c.meetingId(), "DECISION", decision.id(), link.id());
    }

    private static String truncate(String s, int max) {
        if (s == null || s.isBlank()) return "Decision from meeting note";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
