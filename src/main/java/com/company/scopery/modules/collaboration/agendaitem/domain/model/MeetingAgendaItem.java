package com.company.scopery.modules.collaboration.agendaitem.domain.model;
import com.company.scopery.modules.collaboration.agendaitem.domain.enums.AgendaItemStatus;
import java.time.Instant; import java.util.UUID;
public record MeetingAgendaItem(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, String title, String description,
        UUID ownerUserId, AgendaItemStatus status, int sortOrder, Integer timeboxMinutes, boolean clientVisible,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingAgendaItem create(UUID workspaceId, UUID projectId, UUID meetingId, String title, String description,
                                           UUID ownerUserId, int sortOrder, Integer timebox, boolean clientVisible) {
        Instant now = Instant.now();
        return new MeetingAgendaItem(UUID.randomUUID(), workspaceId, projectId, meetingId, title, description,
                ownerUserId, AgendaItemStatus.OPEN, sortOrder, timebox, clientVisible, null, null, 0, now, now);
    }
    public MeetingAgendaItem update(String title, String description, UUID ownerUserId, AgendaItemStatus status,
                                    int sortOrder, Integer timebox, boolean clientVisible) {
        return new MeetingAgendaItem(id, workspaceId, projectId, meetingId, title, description, ownerUserId, status,
                sortOrder, timebox, clientVisible, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public MeetingAgendaItem withSortOrder(int order) {
        return update(title, description, ownerUserId, status, order, timeboxMinutes, clientVisible);
    }
    public MeetingAgendaItem archive(UUID actorId) {
        Instant now = Instant.now();
        return new MeetingAgendaItem(id, workspaceId, projectId, meetingId, title, description, ownerUserId, status,
                sortOrder, timeboxMinutes, clientVisible, now, actorId, version, createdAt, now);
    }
}
