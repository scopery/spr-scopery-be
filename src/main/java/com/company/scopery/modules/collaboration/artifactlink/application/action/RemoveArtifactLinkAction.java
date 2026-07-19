package com.company.scopery.modules.collaboration.artifactlink.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLinkRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.artifactlink.application.command.RemoveArtifactLinkCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RemoveArtifactLinkAction {
    private final MeetingArtifactLinkRepository links; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public RemoveArtifactLinkAction(MeetingArtifactLinkRepository links, CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.links=links; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public void execute(RemoveArtifactLinkCommand c) {
        authorization.requireLinkManage(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var link = links.findByIdAndMeetingId(c.linkId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.linkNotFound(c.linkId()));
        links.save(link.archive(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.ARTIFACT_LINK, c.linkId(), CollaborationActivityActions.LINK_REMOVED, "Link removed");
    }
}
