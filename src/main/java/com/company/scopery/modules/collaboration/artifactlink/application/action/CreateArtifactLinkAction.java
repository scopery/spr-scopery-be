package com.company.scopery.modules.collaboration.artifactlink.application.action;
import com.company.scopery.modules.collaboration.artifactlink.application.response.MeetingArtifactLinkResponse;
import com.company.scopery.modules.collaboration.artifactlink.domain.enums.ArtifactLinkType;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.*;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.artifactlink.application.command.CreateArtifactLinkCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateArtifactLinkAction {
    private final MeetingRepository meetings; private final MeetingArtifactLinkRepository links;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateArtifactLinkAction(MeetingRepository meetings, MeetingArtifactLinkRepository links, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.links=links; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingArtifactLinkResponse execute(CreateArtifactLinkCommand c) {
        authorization.requireLinkManage(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        if (links.existsActive(c.meetingId(), c.targetType(), c.targetId())) throw CollaborationExceptions.linkDuplicate();
        var lt = CollaborationEnumParser.parseRequired(ArtifactLinkType.class, c.linkType(), "linkType");
        var link = MeetingArtifactLink.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), c.agendaItemId(), c.noteId(), c.actionItemId(), c.targetType(), c.targetId(), lt);
        link = links.save(link);
        activityLogger.logSuccess(CollaborationEntityTypes.ARTIFACT_LINK, link.id(), CollaborationActivityActions.LINK_CREATED, "Link created");
        return MeetingArtifactLinkResponse.from(link);
    }
}
