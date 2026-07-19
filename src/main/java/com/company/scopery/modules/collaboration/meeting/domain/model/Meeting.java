package com.company.scopery.modules.collaboration.meeting.domain.model;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingStatus;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import java.time.Instant; import java.util.UUID;
public record Meeting(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingSeriesId, String title, String description,
        MeetingType meetingType, MeetingStatus status, Instant startAt, Instant endAt, String timezone,
        String location, String meetingUrl, UUID organizerUserId, boolean clientVisible, boolean internalOnly,
        Instant cancelledAt, UUID cancelledBy, String cancelReason, Instant archivedAt, UUID archivedBy,
        String traceId, int version, Instant createdAt, Instant updatedAt
) {
    public static Meeting create(UUID workspaceId, UUID projectId, UUID seriesId, String title, String description,
                                 MeetingType type, Instant startAt, Instant endAt, String timezone, String location,
                                 String meetingUrl, UUID organizerUserId, boolean clientVisible) {
        Instant now = Instant.now();
        return new Meeting(UUID.randomUUID(), workspaceId, projectId, seriesId, title, description, type,
                MeetingStatus.SCHEDULED, startAt, endAt, timezone, location, meetingUrl, organizerUserId,
                clientVisible, !clientVisible, null, null, null, null, null, null, 0, now, now);
    }
    public Meeting update(String title, String description, MeetingType type, Instant startAt, Instant endAt,
                          String timezone, String location, String meetingUrl, boolean clientVisible) {
        requireEditable();
        return new Meeting(id, workspaceId, projectId, meetingSeriesId, title, description, type, status, startAt, endAt,
                timezone, location, meetingUrl, organizerUserId, clientVisible, !clientVisible, cancelledAt, cancelledBy,
                cancelReason, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public Meeting start() {
        if (status != MeetingStatus.SCHEDULED && status != MeetingStatus.DRAFT)
            throw new IllegalStateException("Only DRAFT/SCHEDULED meetings can start");
        return withStatus(MeetingStatus.IN_PROGRESS);
    }
    public Meeting complete() {
        if (status != MeetingStatus.IN_PROGRESS && status != MeetingStatus.SCHEDULED)
            throw new IllegalStateException("Only IN_PROGRESS/SCHEDULED meetings can complete");
        return withStatus(MeetingStatus.COMPLETED);
    }
    public Meeting cancel(UUID actorId, String reason) {
        if (status == MeetingStatus.COMPLETED || status == MeetingStatus.CANCELLED || status == MeetingStatus.ARCHIVED)
            throw new IllegalStateException("Cannot cancel meeting in status " + status);
        Instant now = Instant.now();
        return new Meeting(id, workspaceId, projectId, meetingSeriesId, title, description, meetingType,
                MeetingStatus.CANCELLED, startAt, endAt, timezone, location, meetingUrl, organizerUserId,
                clientVisible, internalOnly, now, actorId, reason, archivedAt, archivedBy, traceId, version, createdAt, now);
    }
    public Meeting archive(UUID actorId) {
        Instant now = Instant.now();
        return new Meeting(id, workspaceId, projectId, meetingSeriesId, title, description, meetingType,
                MeetingStatus.ARCHIVED, startAt, endAt, timezone, location, meetingUrl, organizerUserId,
                clientVisible, internalOnly, cancelledAt, cancelledBy, cancelReason, now, actorId, traceId, version, createdAt, now);
    }
    private Meeting withStatus(MeetingStatus s) {
        return new Meeting(id, workspaceId, projectId, meetingSeriesId, title, description, meetingType, s,
                startAt, endAt, timezone, location, meetingUrl, organizerUserId, clientVisible, internalOnly,
                cancelledAt, cancelledBy, cancelReason, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    private void requireEditable() {
        if (status == MeetingStatus.CANCELLED || status == MeetingStatus.ARCHIVED || status == MeetingStatus.COMPLETED)
            throw new IllegalStateException("Meeting not editable in status " + status);
    }
}
