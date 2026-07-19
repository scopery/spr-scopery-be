package com.company.scopery.modules.collaboration.participant.application.action;
import com.company.scopery.modules.collaboration.participant.application.response.MeetingParticipantResponse;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipantRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.participant.application.command.MarkParticipantAttendedCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class MarkParticipantAttendedAction {
    private final MeetingParticipantRepository participants;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public MarkParticipantAttendedAction(MeetingParticipantRepository participants, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.participants=participants; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingParticipantResponse execute(MarkParticipantAttendedCommand c) {
        authorization.requireParticipantManage(c.projectId());
        var p = participants.findByIdAndMeetingId(c.participantId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.participantNotFound(c.participantId()));
        p = participants.save(p.markAttended());
        activityLogger.logSuccess(CollaborationEntityTypes.PARTICIPANT, p.id(), CollaborationActivityActions.PARTICIPANT_MARKED_ATTENDED, "Marked attended");
        return MeetingParticipantResponse.from(p);
    }
}
