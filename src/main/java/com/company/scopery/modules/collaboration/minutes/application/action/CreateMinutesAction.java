package com.company.scopery.modules.collaboration.minutes.application.action;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.minutes.application.command.CreateMinutesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateMinutesAction {
    private final MeetingRepository meetings; private final MeetingMinutesRepository minutesRepo;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateMinutesAction(MeetingRepository meetings, MeetingMinutesRepository minutesRepo,
                               CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.minutesRepo=minutesRepo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingMinutesResponse execute(CreateMinutesCommand c) {
        authorization.requireMinutesUpdate(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var m = MeetingMinutes.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), c.summary(), c.decisions(), c.actions(), c.clientSummary());
        m = minutesRepo.save(m);
        activityLogger.logSuccess(CollaborationEntityTypes.MINUTES, m.id(), CollaborationActivityActions.MINUTES_CREATED, "Minutes created");
        return MeetingMinutesResponse.from(m);
    }
}
