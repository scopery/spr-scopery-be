package com.company.scopery.modules.servicesupport.assignment.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignment;
import com.company.scopery.modules.servicesupport.assignment.infrastructure.persistence.SupportAssignmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SupportAssignmentPersistenceMapper {
    public SupportAssignmentJpaEntity toJpa(SupportAssignment d) {
        SupportAssignmentJpaEntity e = new SupportAssignmentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setAssignmentType(d.assignmentType()); e.setAssigneeUserId(d.assigneeUserId());
        e.setResourceProfileId(d.resourceProfileId()); e.setStatus(d.status()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportAssignment toDomain(SupportAssignmentJpaEntity e) {
        return new SupportAssignment(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getAssignmentType(),
                e.getAssigneeUserId(), e.getResourceProfileId(), e.getStatus(), e.getCreatedAt());
    }
}
