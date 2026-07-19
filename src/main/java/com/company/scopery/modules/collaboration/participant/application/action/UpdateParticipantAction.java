package com.company.scopery.modules.collaboration.participant.application.action;
import com.company.scopery.modules.collaboration.participant.application.response.MeetingParticipantResponse;
import com.company.scopery.modules.collaboration.participant.domain.enums.*;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipantRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.participant.application.command.UpdateParticipantCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateParticipantAction {
    private final MeetingParticipantRepository participants;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public UpdateParticipantAction(MeetingParticipantRepository participants, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.participants=participants; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingParticipantResponse execute(UpdateParticipantCommand c) {
        authorization.requireParticipantManage(c.projectId());
        var p = participants.findByIdAndMeetingId(c.participantId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.participantNotFound(c.participantId()));
        var pr = CollaborationEnumParser.parseRequired(ParticipantRole.class, c.role(), "participantRole");
        var att = CollaborationEnumParser.parseRequired(AttendanceStatus.class, c.attendance(), "attendanceStatus");
        p = participants.save(p.update(pr, att, Boolean.TRUE.equals(c.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.PARTICIPANT, p.id(), CollaborationActivityActions.PARTICIPANT_UPDATED, "Participant updated");
        return MeetingParticipantResponse.from(p);
    }
}
