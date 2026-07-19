package com.company.scopery.modules.clientportal.portalmeeting.application.response;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes;
import java.time.Instant; import java.util.UUID;
public record PortalMeetingMinutesResponse(UUID id, UUID meetingId, String status, String clientVisibleSummary,
                                           Instant approvedAt, Instant createdAt) {
    public static PortalMeetingMinutesResponse from(MeetingMinutes m) {
        return new PortalMeetingMinutesResponse(m.id(), m.meetingId(), m.status().name(), m.clientVisibleSummary(),
                m.approvedAt(), m.createdAt());
    }
}
