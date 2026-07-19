package com.company.scopery.modules.servicesupport.snapshot.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportMetricSnapshotRepository {
    SupportMetricSnapshot save(SupportMetricSnapshot snapshot);
    Optional<SupportMetricSnapshot> findById(UUID id);
    List<SupportMetricSnapshot> findByWorkspaceId(UUID workspaceId);
}
