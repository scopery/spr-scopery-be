package com.company.scopery.modules.raid.raidaction.domain.model;
import com.company.scopery.modules.raid.raidaction.domain.enums.RaidActionStatus;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record RaidAction(UUID id, UUID raidItemId, UUID projectId, String title, String description, UUID ownerUserId,
        LocalDate dueDate, RaidActionStatus status, UUID linkedTaskId, String completionNote, Instant completedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static RaidAction create(UUID raidItemId, UUID projectId, String title, String description, UUID ownerUserId, LocalDate dueDate) {
        Instant now = Instant.now();
        return new RaidAction(UUID.randomUUID(), raidItemId, projectId, title, description, ownerUserId, dueDate,
                RaidActionStatus.OPEN, null, null, null, 0, now, now);
    }
    public RaidAction complete(String note) {
        return new RaidAction(id, raidItemId, projectId, title, description, ownerUserId, dueDate, RaidActionStatus.DONE,
                linkedTaskId, note, Instant.now(), version, createdAt, Instant.now());
    }
    public RaidAction withLinkedTask(UUID taskId) {
        return new RaidAction(id, raidItemId, projectId, title, description, ownerUserId, dueDate, status, taskId,
                completionNote, completedAt, version, createdAt, Instant.now());
    }
    public RaidAction update(String title, String description, UUID ownerUserId, LocalDate dueDate) {
        return new RaidAction(id, raidItemId, projectId, title, description, ownerUserId, dueDate, status, linkedTaskId,
                completionNote, completedAt, version, createdAt, Instant.now());
    }
    public RaidAction cancel() {
        return new RaidAction(id, raidItemId, projectId, title, description, ownerUserId, dueDate, RaidActionStatus.CANCELLED,
                linkedTaskId, completionNote, completedAt, version, createdAt, Instant.now());
    }
}
