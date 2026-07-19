package com.company.scopery.modules.servicesupport.snapshot.application.response;
import com.company.scopery.modules.servicesupport.snapshot.domain.model.SupportMetricSnapshot;
import java.time.Instant; import java.util.UUID;
public record SupportMetricSnapshotResponse(UUID id, UUID workspaceId, UUID projectId, String snapshotSource,
        Instant snapshotAt, Instant createdAt) {
    public static SupportMetricSnapshotResponse from(SupportMetricSnapshot d) {
        return new SupportMetricSnapshotResponse(d.id(), d.workspaceId(), d.projectId(), d.snapshotSource(),
                d.snapshotAt(), d.createdAt());
    }
}
