package com.company.scopery.modules.scope.mapping.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DeliverableTaskMappingRepository {
    DeliverableTaskMapping save(DeliverableTaskMapping mapping);
    Optional<DeliverableTaskMapping> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeliverableTaskMapping> findActiveByDeliverableId(UUID deliverableId);
}
