package com.company.scopery.modules.scope.mapping.domain.model;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import java.time.Instant; import java.util.UUID;
public record DeliverableTaskMapping(UUID id, UUID deliverableId, UUID projectId, UUID taskId, MappingType mappingType,
        Instant archivedAt, int version, Instant createdAt) {
    public static DeliverableTaskMapping create(UUID deliverableId, UUID projectId, UUID taskId, MappingType mappingType) {
        MappingType type = mappingType == null ? MappingType.SUPPORTING : mappingType;
        return new DeliverableTaskMapping(UUID.randomUUID(), deliverableId, projectId, taskId, type, null, 0, Instant.now());
    }
    public DeliverableTaskMapping archive() {
        if (archivedAt != null) throw new IllegalStateException("Mapping already archived");
        return new DeliverableTaskMapping(id, deliverableId, projectId, taskId, mappingType, Instant.now(), version, createdAt);
    }
    public boolean isActive() { return archivedAt == null; }
}
