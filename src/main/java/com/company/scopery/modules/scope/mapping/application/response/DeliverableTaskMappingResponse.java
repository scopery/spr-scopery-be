package com.company.scopery.modules.scope.mapping.application.response;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMapping;
import java.time.Instant; import java.util.UUID;
public record DeliverableTaskMappingResponse(UUID id, UUID deliverableId, UUID projectId, UUID taskId,
        String mappingType, Instant archivedAt, Instant createdAt) {
    public static DeliverableTaskMappingResponse from(DeliverableTaskMapping m) {
        return new DeliverableTaskMappingResponse(m.id(), m.deliverableId(), m.projectId(), m.taskId(),
                m.mappingType().name(), m.archivedAt(), m.createdAt());
    }
}
