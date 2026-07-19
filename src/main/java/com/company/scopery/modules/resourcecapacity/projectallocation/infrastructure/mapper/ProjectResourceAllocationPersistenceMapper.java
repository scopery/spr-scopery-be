package com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.mapper;

import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.ProjectResourceAllocationStatus;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.persistence.ProjectResourceAllocationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectResourceAllocationPersistenceMapper {

    public ProjectResourceAllocation toDomain(ProjectResourceAllocationJpaEntity entity) {
        return new ProjectResourceAllocation(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getWorkspaceMemberId(),
                entity.getUserId(),
                entity.getAllocationPercent(),
                AllocationType.valueOf(entity.getAllocationType()),
                ProjectResourceAllocationStatus.valueOf(entity.getStatus()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getNotes(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectResourceAllocationJpaEntity toJpaEntity(ProjectResourceAllocation domain) {
        ProjectResourceAllocationJpaEntity entity = new ProjectResourceAllocationJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setWorkspaceMemberId(domain.workspaceMemberId());
        entity.setUserId(domain.userId());
        entity.setAllocationPercent(domain.allocationPercent());
        entity.setAllocationType(domain.allocationType().name());
        entity.setStatus(domain.status().name());
        entity.setStartDate(domain.startDate());
        entity.setEndDate(domain.endDate());
        entity.setNotes(domain.notes());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
