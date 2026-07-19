package com.company.scopery.modules.collaboration.minutes.application.service;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingMinutesQueryService {
    private final MeetingMinutesRepository minutesRepo; private final CollaborationAuthorizationService authorization;
    public MeetingMinutesQueryService(MeetingMinutesRepository minutesRepo, CollaborationAuthorizationService authorization) {
        this.minutesRepo=minutesRepo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<MeetingMinutesResponse> list(UUID projectId, UUID meetingId) {
        authorization.requireMinutesView(projectId);
        return minutesRepo.findByMeetingId(meetingId).stream().map(MeetingMinutesResponse::from).toList();
    }
}
