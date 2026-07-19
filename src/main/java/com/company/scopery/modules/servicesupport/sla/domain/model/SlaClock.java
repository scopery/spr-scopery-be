package com.company.scopery.modules.servicesupport.sla.domain.model;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import java.time.Instant; import java.util.UUID;
public record SlaClock(UUID id, UUID workspaceId, UUID supportCaseId, UUID slaPolicyId, String clockType,
        Instant startedAt, Instant dueAt, Instant pausedAt, Instant completedAt, String status, int version, Instant createdAt) {
    public static SlaClock start(UUID workspaceId, UUID caseId, UUID policyId, String clockType, int resolveMinutes) {
        Instant now = Instant.now();
        return new SlaClock(UUID.randomUUID(), workspaceId, caseId, policyId, clockType, now,
                SlaClockService.dueAt(now, resolveMinutes), null, null, "RUNNING", 0, now);
    }
    public SlaClock pause() {
        if (!"RUNNING".equals(status)) throw new IllegalStateException("invalid");
        return new SlaClock(id, workspaceId, supportCaseId, slaPolicyId, clockType, startedAt, dueAt, Instant.now(), null, "PAUSED", version, createdAt);
    }
    public SlaClock complete() {
        return new SlaClock(id, workspaceId, supportCaseId, slaPolicyId, clockType, startedAt, dueAt, pausedAt, Instant.now(), "COMPLETED", version, createdAt);
    }
    public SlaClock markBreached() {
        return new SlaClock(id, workspaceId, supportCaseId, slaPolicyId, clockType, startedAt, dueAt, pausedAt, completedAt, "BREACHED", version, createdAt);
    }
}
