package com.company.scopery.modules.clientportal.portalmeeting.application.service;
import com.company.scopery.modules.clientportal.portalmeeting.application.response.PortalMeetingMinutesResponse;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;

@Service
public class PortalMeetingQueryService {
    private final MeetingRepository meetings;
    private final MeetingMinutesRepository minutes;
    private final PortalGrantEnforcementService grantEnforcement;

    public PortalMeetingQueryService(MeetingRepository meetings, MeetingMinutesRepository minutes,
                                     PortalGrantEnforcementService grantEnforcement) {
        this.meetings = meetings; this.minutes = minutes; this.grantEnforcement = grantEnforcement;
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> list(UUID projectId) {
        grantEnforcement.requireActiveGrant(projectId);
        return meetings.findByProjectId(projectId).stream()
                .filter(m -> m.clientVisible() && m.archivedAt() == null)
                .map(MeetingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingResponse get(UUID projectId, UUID meetingId) {
        grantEnforcement.requireActiveGrant(projectId);
        var meeting = meetings.findByIdAndProjectId(meetingId, projectId)
                .orElseThrow(ClientPortalExceptions::accessDenied);
        if (!meeting.clientVisible() || meeting.archivedAt() != null) throw ClientPortalExceptions.accessDenied();
        return MeetingResponse.from(meeting);
    }

    @Transactional(readOnly = true)
    public List<PortalMeetingMinutesResponse> listMinutes(UUID projectId, UUID meetingId) {
        grantEnforcement.requireActiveGrant(projectId);
        var meeting = meetings.findByIdAndProjectId(meetingId, projectId)
                .orElseThrow(ClientPortalExceptions::accessDenied);
        if (!meeting.clientVisible() || meeting.archivedAt() != null) throw ClientPortalExceptions.accessDenied();
        return minutes.findByMeetingId(meetingId).stream()
                .filter(m -> m.clientVisibleSummary() != null && !m.clientVisibleSummary().isBlank())
                .map(PortalMeetingMinutesResponse::from)
                .toList();
    }
}
