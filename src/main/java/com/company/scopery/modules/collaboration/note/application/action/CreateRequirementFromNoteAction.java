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
import com.company.scopery.modules.traceability.requirement.application.action.CreateRequirementAction;
import com.company.scopery.modules.traceability.requirement.application.command.CreateRequirementCommand;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.CreateRequirementFromNoteCommand;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRequirementFromNoteAction {
    private final MeetingRepository meetings;
    private final MeetingNoteRepository notes;
    private final MeetingArtifactLinkRepository links;
    private final CreateRequirementAction createRequirement;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;

    public CreateRequirementFromNoteAction(MeetingRepository meetings, MeetingNoteRepository notes,
                                           MeetingArtifactLinkRepository links, CreateRequirementAction createRequirement,
                                           CollaborationAuthorizationService authorization,
                                           CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.notes = notes; this.links = links;
        this.createRequirement = createRequirement; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public NoteConversionResponse execute(CreateRequirementFromNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var note = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        if (note.archivedAt() != null) throw CollaborationExceptions.noteArchived(c.noteId());
        String t = (c.title() == null || c.title().isBlank()) ? truncate(note.body(), 200) : c.title().trim();
        String d = (c.description() == null || c.description().isBlank()) ? note.body() : c.description().trim();
        String reqType = (c.requirementType() == null || c.requirementType().isBlank()) ? "FUNCTIONAL" : c.requirementType().trim();
        String prio = (c.priority() == null || c.priority().isBlank()) ? "MEDIUM" : c.priority().trim();
        var req = createRequirement.execute(new CreateRequirementCommand(
                c.projectId(), c.applicationId(), c.code(), t, d, reqType, prio));
        if (links.existsActive(c.meetingId(), "REQUIREMENT", req.id())) throw CollaborationExceptions.linkDuplicate();
        var link = links.save(MeetingArtifactLink.create(
                note.workspaceId(), note.projectId(), note.meetingId(), note.agendaItemId(), note.id(), null,
                "REQUIREMENT", req.id(), ArtifactLinkType.DISCUSSED));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, note.id(),
                CollaborationActivityActions.NOTE_CONVERTED_REQUIREMENT, "Note converted to requirement " + req.id());
        return new NoteConversionResponse(note.id(), c.meetingId(), "REQUIREMENT", req.id(), link.id());
    }

    private static String truncate(String s, int max) {
        if (s == null || s.isBlank()) return "Requirement from meeting note";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
