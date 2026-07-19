package com.company.scopery.modules.collaboration.participant.application.service;
import com.company.scopery.modules.collaboration.participant.application.response.MeetingParticipantResponse;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipantRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingParticipantQueryService {
    private final MeetingParticipantRepository participants; private final CollaborationAuthorizationService authorization;
    public MeetingParticipantQueryService(MeetingParticipantRepository participants, CollaborationAuthorizationService authorization) {
        this.participants=participants; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<MeetingParticipantResponse> list(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return participants.findByMeetingId(meetingId).stream().map(MeetingParticipantResponse::from).toList();
    }
}
