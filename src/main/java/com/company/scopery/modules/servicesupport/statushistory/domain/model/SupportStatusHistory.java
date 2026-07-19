package com.company.scopery.modules.servicesupport.statushistory.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportStatusHistory(UUID id, UUID workspaceId, UUID supportCaseId, String fromStatus, String toStatus,
        String reason, Instant changedAt, UUID changedBy, String visibility,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportStatusHistory record(UUID workspaceId, UUID caseId, String fromStatus, String toStatus, String visibility) {
        Instant now = Instant.now();
        return new SupportStatusHistory(UUID.randomUUID(), workspaceId, caseId, fromStatus, toStatus, null,
                now, null, visibility, 0, now, now);
    }
}
