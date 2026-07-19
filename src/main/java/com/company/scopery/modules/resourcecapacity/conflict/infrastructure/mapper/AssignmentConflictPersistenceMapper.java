package com.company.scopery.modules.resourcecapacity.conflict.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflict;
import com.company.scopery.modules.resourcecapacity.conflict.infrastructure.persistence.AssignmentConflictJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AssignmentConflictPersistenceMapper {
    public AssignmentConflictJpaEntity toJpaEntity(AssignmentConflict d) {
        AssignmentConflictJpaEntity e = new AssignmentConflictJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setResourceProfileId(d.resourceProfileId());
        e.setConflictType(d.conflictType()); e.setSeverity(d.severity()); e.setDescription(d.description()); e.setStatus(d.status());
        e.setDetectedAt(d.detectedAt()); e.setAcknowledgedAt(d.acknowledgedAt()); e.setResolvedAt(d.resolvedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public AssignmentConflict toDomain(AssignmentConflictJpaEntity e) {
        return new AssignmentConflict(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getResourceProfileId(), e.getConflictType(),
                e.getSeverity(), e.getDescription(), e.getStatus(), e.getDetectedAt(), e.getAcknowledgedAt(), e.getResolvedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
