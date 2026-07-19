package com.company.scopery.modules.collaboration.meeting.application.response;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import java.time.Instant; import java.util.UUID;
public record MeetingResponse(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingSeriesId, String title, String description,
        String meetingType, String status, Instant startAt, Instant endAt, String timezone, String location,
        String meetingUrl, UUID organizerUserId, boolean clientVisible, boolean internalOnly,
        Instant cancelledAt, String cancelReason, Instant archivedAt, Instant createdAt, Instant updatedAt, int version
) {
    public static MeetingResponse from(Meeting m) {
        return new MeetingResponse(m.id(), m.workspaceId(), m.projectId(), m.meetingSeriesId(), m.title(), m.description(),
                m.meetingType().name(), m.status().name(), m.startAt(), m.endAt(), m.timezone(), m.location(), m.meetingUrl(),
                m.organizerUserId(), m.clientVisible(), m.internalOnly(), m.cancelledAt(), m.cancelReason(),
                m.archivedAt(), m.createdAt(), m.updatedAt(), m.version());
    }
}
