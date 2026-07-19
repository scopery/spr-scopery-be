package com.company.scopery.modules.servicesupport.snapshot.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record SupportMetricSnapshot(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        LocalDate periodStart, LocalDate periodEnd, int openCases, int newCases, int resolvedCases, int closedCases,
        int slaAtRisk, int slaBreached, int criticalIncidents,
        BigDecimal avgFirstResponseMinutes, BigDecimal avgResolutionMinutes, int maintenanceWindowsPlanned,
        BigDecimal supportEffortHours, BigDecimal supportCostInput, String currency, String snapshotSource,
        Instant snapshotAt, int version, Instant createdAt) {
    public static SupportMetricSnapshot create(UUID workspaceId, UUID projectId, String snapshotSource) {
        Instant now = Instant.now();
        return new SupportMetricSnapshot(UUID.randomUUID(), workspaceId, projectId, null,
                null, null, 0, 0, 0, 0, 0, 0, 0, null, null, 0,
                BigDecimal.ZERO, null, null, snapshotSource, now, 0, now);
    }
}
