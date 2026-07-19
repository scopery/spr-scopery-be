package com.company.scopery.modules.clientportal.uat.infrastructure.mapper;
import com.company.scopery.modules.clientportal.uat.domain.enums.ClientUatAssignmentStatus;
import com.company.scopery.modules.clientportal.uat.domain.model.ClientUatAssignment;
import com.company.scopery.modules.clientportal.uat.infrastructure.persistence.ClientUatAssignmentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ClientUatAssignmentPersistenceMapper {
    public ClientUatAssignment toDomain(ClientUatAssignmentJpaEntity e) {
        return new ClientUatAssignment(e.getId(), e.getProjectId(), e.getTestCaseId(), e.getTestRunId(), e.getPortalAccountId(),
                e.getStatus() != null ? ClientUatAssignmentStatus.valueOf(e.getStatus()) : ClientUatAssignmentStatus.ASSIGNED,
                e.getNotes(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ClientUatAssignmentJpaEntity toJpaEntity(ClientUatAssignment d) {
        ClientUatAssignmentJpaEntity e = new ClientUatAssignmentJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestCaseId(d.testCaseId()); e.setTestRunId(d.testRunId());
        e.setPortalAccountId(d.portalAccountId()); e.setStatus(d.status().name()); e.setNotes(d.notes()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
