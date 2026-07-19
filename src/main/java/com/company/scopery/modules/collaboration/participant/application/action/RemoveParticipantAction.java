package com.company.scopery.modules.collaboration.participant.application.action;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipantRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.participant.application.command.RemoveParticipantCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RemoveParticipantAction {
    private final MeetingParticipantRepository participants;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public RemoveParticipantAction(MeetingParticipantRepository participants, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.participants=participants; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public void execute(RemoveParticipantCommand c) {
        authorization.requireParticipantManage(c.projectId());
        var p = participants.findByIdAndMeetingId(c.participantId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.participantNotFound(c.participantId()));
        participants.delete(p);
        activityLogger.logSuccess(CollaborationEntityTypes.PARTICIPANT, c.participantId(), CollaborationActivityActions.PARTICIPANT_REMOVED, "Participant removed");
    }
}
