package com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.enums.*;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.ActualEffortRecord;
import com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.persistence.ActualEffortRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ActualEffortPersistenceMapper {
    public ActualEffortRecordJpaEntity toJpaEntity(ActualEffortRecord d) {
        ActualEffortRecordJpaEntity e = new ActualEffortRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setResourceProfileId(d.resourceProfileId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setEffortDate(d.effortDate()); e.setEffortHours(d.effortHours());
        e.setInputMode(d.inputMode().name()); e.setDescription(d.description()); e.setStatus(d.status().name());
        e.setCancelledAt(d.cancelledAt()); e.setCancelledBy(d.cancelledBy()); e.setCancellationReason(d.cancellationReason());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ActualEffortRecord toDomain(ActualEffortRecordJpaEntity e) {
        return new ActualEffortRecord(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getResourceProfileId(), e.getTargetType(), e.getTargetId(),
                e.getEffortDate(), e.getEffortHours(), ActualEffortInputMode.valueOf(e.getInputMode()), e.getDescription(),
                ActualEffortStatus.valueOf(e.getStatus()), e.getCancelledAt(), e.getCancelledBy(), e.getCancellationReason(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
