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
import com.company.scopery.modules.raid.raiditem.application.action.CreateRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.command.CreateRaidItemCommand;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.note.application.command.CreateRaidItemFromNoteCommand;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRaidItemFromNoteAction {
    private final MeetingRepository meetings;
    private final MeetingNoteRepository notes;
    private final MeetingArtifactLinkRepository links;
    private final CreateRaidItemAction createRaidItem;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;

    public CreateRaidItemFromNoteAction(MeetingRepository meetings, MeetingNoteRepository notes,
                                        MeetingArtifactLinkRepository links, CreateRaidItemAction createRaidItem,
                                        CollaborationAuthorizationService authorization,
                                        CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.notes = notes; this.links = links;
        this.createRaidItem = createRaidItem; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public NoteConversionResponse execute(CreateRaidItemFromNoteCommand c) {
        authorization.requireNoteManage(c.projectId());
        meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var note = notes.findByIdAndMeetingId(c.noteId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.noteNotFound(c.noteId()));
        if (note.archivedAt() != null) throw CollaborationExceptions.noteArchived(c.noteId());
        String raidType = (c.type() == null || c.type().isBlank()) ? "RISK" : c.type().trim();
        String t = (c.title() == null || c.title().isBlank()) ? truncate(note.body(), 200) : c.title().trim();
        String d = (c.description() == null || c.description().isBlank()) ? note.body() : c.description().trim();
        var item = createRaidItem.execute(new CreateRaidItemCommand(
                c.projectId(), raidType, t, c.code(), d, c.ownerUserId(),
                null, null, null, null, null, null, null, null, null, null, null, null));
        if (links.existsActive(c.meetingId(), "RAID_ITEM", item.id())) throw CollaborationExceptions.linkDuplicate();
        var link = links.save(MeetingArtifactLink.create(
                note.workspaceId(), note.projectId(), note.meetingId(), note.agendaItemId(), note.id(), null,
                "RAID_ITEM", item.id(), ArtifactLinkType.FOLLOW_UP_FOR));
        activityLogger.logSuccess(CollaborationEntityTypes.NOTE, note.id(),
                CollaborationActivityActions.NOTE_CONVERTED_RAID, "Note converted to RAID " + item.id());
        return new NoteConversionResponse(note.id(), c.meetingId(), "RAID_ITEM", item.id(), link.id());
    }

    private static String truncate(String s, int max) {
        if (s == null || s.isBlank()) return "RAID item from meeting note";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
