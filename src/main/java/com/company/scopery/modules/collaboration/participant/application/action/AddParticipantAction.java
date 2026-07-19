package com.company.scopery.modules.collaboration.participant.application.action;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.participant.application.response.MeetingParticipantResponse;
import com.company.scopery.modules.collaboration.participant.domain.enums.*;
import com.company.scopery.modules.collaboration.participant.domain.model.*;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.participant.application.command.AddParticipantCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AddParticipantAction {
    private final MeetingRepository meetings; private final MeetingParticipantRepository participants;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public AddParticipantAction(MeetingRepository meetings, MeetingParticipantRepository participants,
                                CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.participants=participants; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingParticipantResponse execute(AddParticipantCommand c) {
        authorization.requireParticipantManage(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var tt = CollaborationEnumParser.parseRequired(ParticipantTargetType.class, c.targetType(), "targetType");
        var pr = CollaborationEnumParser.parseRequired(ParticipantRole.class, c.role(), "participantRole");
        var p = MeetingParticipant.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), tt, c.targetId(), c.displayName(), c.email(), pr, Boolean.TRUE.equals(c.clientVisible()));
        p = participants.save(p);
        activityLogger.logSuccess(CollaborationEntityTypes.PARTICIPANT, p.id(), CollaborationActivityActions.PARTICIPANT_ADDED, "Participant added");
        return MeetingParticipantResponse.from(p);
    }
}
