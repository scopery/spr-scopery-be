package com.company.scopery.modules.scope.mapping.infrastructure.mapper;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMapping;
import com.company.scopery.modules.scope.mapping.infrastructure.persistence.DeliverableTaskMappingJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeliverableTaskMappingPersistenceMapper {
    public DeliverableTaskMapping toDomain(DeliverableTaskMappingJpaEntity e) {
        return new DeliverableTaskMapping(e.getId(), e.getDeliverableId(), e.getProjectId(), e.getTaskId(),
                MappingType.valueOf(e.getMappingType()), e.getArchivedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public DeliverableTaskMappingJpaEntity toJpaEntity(DeliverableTaskMapping d) {
        DeliverableTaskMappingJpaEntity e = new DeliverableTaskMappingJpaEntity();
        e.setId(d.id()); e.setDeliverableId(d.deliverableId()); e.setProjectId(d.projectId());
        e.setTaskId(d.taskId()); e.setMappingType(d.mappingType().name());
        e.setArchivedAt(d.archivedAt()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
