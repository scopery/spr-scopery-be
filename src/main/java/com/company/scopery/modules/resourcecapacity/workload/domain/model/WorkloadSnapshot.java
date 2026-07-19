package com.company.scopery.modules.resourcecapacity.workload.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record WorkloadSnapshot(UUID id, UUID workspaceId, UUID projectId, BigDecimal totalCapacityHours, BigDecimal totalAllocatedHours,
        BigDecimal totalEstimatedEffortHours, BigDecimal totalForecastEffortHours, BigDecimal totalActualObservedEffortHours,
        BigDecimal capacityGapHours, int overloadCount, int understaffedRoleCount, BigDecimal costForecastInput,
        String snapshotSource, Instant snapshotAt, int version, Instant createdAt) {
    public static WorkloadSnapshot create(UUID workspaceId, UUID projectId, BigDecimal capacity, BigDecimal allocated,
                                          BigDecimal estimated, BigDecimal forecast, BigDecimal actual, BigDecimal gap,
                                          int overloadCount, BigDecimal costInput, String source) {
        Instant now = Instant.now();
        return new WorkloadSnapshot(UUID.randomUUID(), workspaceId, projectId, capacity, allocated, estimated, forecast, actual,
                gap, overloadCount, 0, costInput, source, now, 0, now);
    }
}
