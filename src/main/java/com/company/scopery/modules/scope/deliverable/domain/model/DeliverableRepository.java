package com.company.scopery.modules.scope.deliverable.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DeliverableRepository {
    Deliverable save(Deliverable d);
    Optional<Deliverable> findByIdAndProjectId(UUID id, UUID projectId);
    List<Deliverable> findByProjectId(UUID projectId);
}
