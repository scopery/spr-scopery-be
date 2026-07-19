package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record WorkloadSnapshotResponse(UUID id, UUID projectId, BigDecimal totalCapacityHours, BigDecimal totalForecastEffortHours,
        BigDecimal capacityGapHours, int overloadCount, Instant snapshotAt, String snapshotSource) {}
