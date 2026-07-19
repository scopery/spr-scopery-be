package com.company.scopery.modules.collaboration.meeting.application.action;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.meeting.application.command.CompleteMeetingCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CompleteMeetingAction {
    private final MeetingRepository meetings;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public CompleteMeetingAction(MeetingRepository meetings, CollaborationAuthorizationService authorization,
                  CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public MeetingResponse execute(CompleteMeetingCommand c) {
        authorization.requireMeetingUpdate(c.projectId());
        Meeting meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId())
                .orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        try { meeting = meeting.complete(); }
        catch (IllegalStateException ex) { throw CollaborationExceptions.invalidStatus(ex.getMessage()); }
        meeting = meetings.save(meeting);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING, meeting.id(),
                CollaborationActivityActions.MEETING_COMPLETED, "Meeting complete");
        return MeetingResponse.from(meeting);
    }
}
