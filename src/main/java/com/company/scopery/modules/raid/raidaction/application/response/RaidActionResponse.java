package com.company.scopery.modules.raid.raidaction.application.response;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record RaidActionResponse(UUID id, UUID raidItemId, UUID projectId, String title, String status,
        UUID ownerUserId, LocalDate dueDate, UUID linkedTaskId, Instant completedAt) {
    public static RaidActionResponse from(RaidAction a) {
        return new RaidActionResponse(a.id(), a.raidItemId(), a.projectId(), a.title(), a.status().name(),
                a.ownerUserId(), a.dueDate(), a.linkedTaskId(), a.completedAt());
    }
}
