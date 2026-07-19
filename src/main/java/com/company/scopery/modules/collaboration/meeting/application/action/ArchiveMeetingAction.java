package com.company.scopery.modules.collaboration.meeting.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.meeting.application.command.ArchiveMeetingCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveMeetingAction {
    private final MeetingRepository meetings;
    private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final CollaborationActivityLogger activityLogger;
    public ArchiveMeetingAction(MeetingRepository meetings, CollaborationAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public MeetingResponse execute(ArchiveMeetingCommand c) {
        authorization.requireMeetingArchive(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        Meeting meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId())
                .orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        meeting = meeting.archive(actor.id());
        meeting = meetings.save(meeting);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING, meeting.id(),
                CollaborationActivityActions.MEETING_ARCHIVED, "Meeting archived");
        return MeetingResponse.from(meeting);
    }
}
