package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.time.Instant; import java.util.UUID;
public record SlaBreach(UUID id, UUID workspaceId, UUID supportCaseId, UUID slaClockId, String breachType,
        Instant breachedAt, String status, int version, Instant createdAt) {
    public static SlaBreach open(UUID workspaceId, UUID caseId, UUID clockId, String breachType) {
        Instant now = Instant.now();
        return new SlaBreach(UUID.randomUUID(), workspaceId, caseId, clockId, breachType, now, "OPEN", 0, now);
    }
}
