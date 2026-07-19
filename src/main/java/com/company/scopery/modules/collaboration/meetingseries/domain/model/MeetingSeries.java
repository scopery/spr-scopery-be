package com.company.scopery.modules.collaboration.meetingseries.domain.model;
import com.company.scopery.modules.collaboration.meetingseries.domain.enums.MeetingSeriesCadence;
import com.company.scopery.modules.collaboration.meetingseries.domain.enums.MeetingSeriesStatus;
import java.time.Instant; import java.util.UUID;
public record MeetingSeries(
        UUID id, UUID workspaceId, UUID projectId, String code, String title, String description,
        MeetingSeriesCadence cadence, UUID ownerUserId, MeetingSeriesStatus status, boolean clientVisible,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingSeries create(UUID workspaceId, UUID projectId, String code, String title, String description,
                                       MeetingSeriesCadence cadence, UUID ownerUserId, boolean clientVisible) {
        Instant now = Instant.now();
        return new MeetingSeries(UUID.randomUUID(), workspaceId, projectId, code, title, description, cadence,
                ownerUserId, MeetingSeriesStatus.ACTIVE, clientVisible, null, null, 0, now, now);
    }
    public MeetingSeries update(String title, String description, MeetingSeriesCadence cadence, UUID ownerUserId, boolean clientVisible) {
        return new MeetingSeries(id, workspaceId, projectId, code, title, description, cadence, ownerUserId, status,
                clientVisible, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public MeetingSeries pause() {
        return new MeetingSeries(id, workspaceId, projectId, code, title, description, cadence, ownerUserId,
                MeetingSeriesStatus.PAUSED, clientVisible, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public MeetingSeries archive(UUID actorId) {
        Instant now = Instant.now();
        return new MeetingSeries(id, workspaceId, projectId, code, title, description, cadence, ownerUserId,
                MeetingSeriesStatus.ARCHIVED, clientVisible, now, actorId, version, createdAt, now);
    }
}
