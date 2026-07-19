package com.company.scopery.modules.resourcecapacity.workload.application.response;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshot;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record WorkloadSnapshotApiResponse(UUID id, UUID projectId, BigDecimal totalCapacityHours, BigDecimal totalForecastEffortHours,
        BigDecimal capacityGapHours, int overloadCount, Instant snapshotAt, String snapshotSource) {
    public static WorkloadSnapshotApiResponse from(WorkloadSnapshot s) {
        return new WorkloadSnapshotApiResponse(s.id(), s.projectId(), s.totalCapacityHours(), s.totalForecastEffortHours(),
                s.capacityGapHours(), s.overloadCount(), s.snapshotAt(), s.snapshotSource());
    }
}
