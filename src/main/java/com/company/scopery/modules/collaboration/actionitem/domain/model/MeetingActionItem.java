package com.company.scopery.modules.collaboration.actionitem.domain.model;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record MeetingActionItem(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId, String title, String description,
        String ownerTargetType, UUID ownerTargetId, LocalDate dueDate, ActionItemStatus status,
        UUID linkedTaskId, UUID linkedRaidActionId, Instant completedAt, UUID completedBy, String completionNote,
        boolean clientVisible, Instant archivedAt, UUID archivedBy, String traceId,
        int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingActionItem create(UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId,
                                           String title, String description, String ownerTargetType, UUID ownerTargetId,
                                           LocalDate dueDate, boolean clientVisible) {
        Instant now = Instant.now();
        return new MeetingActionItem(UUID.randomUUID(), workspaceId, projectId, meetingId, agendaItemId, title, description,
                ownerTargetType, ownerTargetId, dueDate, ActionItemStatus.OPEN, null, null, null, null, null,
                clientVisible, null, null, null, 0, now, now);
    }
    public MeetingActionItem update(String title, String description, String ownerTargetType, UUID ownerTargetId,
                                    LocalDate dueDate, ActionItemStatus status, boolean clientVisible) {
        return new MeetingActionItem(id, workspaceId, projectId, meetingId, agendaItemId, title, description,
                ownerTargetType, ownerTargetId, dueDate, status, linkedTaskId, linkedRaidActionId, completedAt,
                completedBy, completionNote, clientVisible, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public MeetingActionItem complete(UUID actorId, String note) {
        Instant now = Instant.now();
        return new MeetingActionItem(id, workspaceId, projectId, meetingId, agendaItemId, title, description,
                ownerTargetType, ownerTargetId, dueDate, ActionItemStatus.DONE, linkedTaskId, linkedRaidActionId,
                now, actorId, note, clientVisible, archivedAt, archivedBy, traceId, version, createdAt, now);
    }
    public MeetingActionItem withLinkedTask(UUID taskId) {
        if (linkedTaskId != null) throw new IllegalStateException("Already linked to task");
        return new MeetingActionItem(id, workspaceId, projectId, meetingId, agendaItemId, title, description,
                ownerTargetType, ownerTargetId, dueDate, status, taskId, linkedRaidActionId, completedAt,
                completedBy, completionNote, clientVisible, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public MeetingActionItem archive(UUID actorId) {
        Instant now = Instant.now();
        return new MeetingActionItem(id, workspaceId, projectId, meetingId, agendaItemId, title, description,
                ownerTargetType, ownerTargetId, dueDate, ActionItemStatus.ARCHIVED, linkedTaskId, linkedRaidActionId,
                completedAt, completedBy, completionNote, clientVisible, now, actorId, traceId, version, createdAt, now);
    }
    public MeetingActionItem markOverdueIfNeeded(LocalDate today) {
        if (status == ActionItemStatus.OPEN || status == ActionItemStatus.IN_PROGRESS) {
            if (dueDate != null && dueDate.isBefore(today)) {
                return new MeetingActionItem(id, workspaceId, projectId, meetingId, agendaItemId, title, description,
                        ownerTargetType, ownerTargetId, dueDate, ActionItemStatus.OVERDUE, linkedTaskId, linkedRaidActionId,
                        completedAt, completedBy, completionNote, clientVisible, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
            }
        }
        return this;
    }
}
