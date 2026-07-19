package com.company.scopery.modules.collaboration.meeting.application.service;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingQueryService {
    private final MeetingRepository meetings;
    private final CollaborationAuthorizationService authorization;
    public MeetingQueryService(MeetingRepository meetings, CollaborationAuthorizationService authorization) {
        this.meetings = meetings; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public MeetingResponse get(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return MeetingResponse.from(meetings.findByIdAndProjectId(meetingId, projectId)
                .orElseThrow(() -> CollaborationExceptions.meetingNotFound(meetingId)));
    }
    @Transactional(readOnly = true)
    public List<MeetingResponse> list(UUID projectId) {
        authorization.requireMeetingView(projectId);
        return meetings.findByProjectId(projectId).stream().map(MeetingResponse::from).toList();
    }
}
