package com.company.scopery.modules.collaboration.meeting.application.action;
import com.company.scopery.modules.collaboration.meeting.application.command.UpdateMeetingCommand;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateMeetingAction {
    private final MeetingRepository meetings;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public UpdateMeetingAction(MeetingRepository meetings, CollaborationAuthorizationService authorization,
                               CollaborationActivityLogger activityLogger) {
        this.meetings = meetings; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public MeetingResponse execute(UpdateMeetingCommand command) {
        authorization.requireMeetingUpdate(command.projectId());
        Meeting meeting = meetings.findByIdAndProjectId(command.meetingId(), command.projectId())
                .orElseThrow(() -> CollaborationExceptions.meetingNotFound(command.meetingId()));
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingType type = CollaborationEnumParser.parseRequired(MeetingType.class, command.meetingType(), "meetingType");
        try {
            meeting = meeting.update(command.title().trim(), command.description(), type, command.startAt(),
                    command.endAt(), command.timezone(), command.location(), command.meetingUrl(),
                    Boolean.TRUE.equals(command.clientVisible()));
        } catch (IllegalStateException ex) {
            throw CollaborationExceptions.invalidStatus(ex.getMessage());
        }
        meeting = meetings.save(meeting);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING, meeting.id(),
                CollaborationActivityActions.MEETING_UPDATED, "Meeting updated");
        return MeetingResponse.from(meeting);
    }
}
