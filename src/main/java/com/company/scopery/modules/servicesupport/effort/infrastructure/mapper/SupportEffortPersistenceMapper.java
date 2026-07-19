package com.company.scopery.modules.servicesupport.effort.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecord;
import com.company.scopery.modules.servicesupport.effort.infrastructure.persistence.SupportEffortRecordJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SupportEffortPersistenceMapper {
    public SupportEffortRecordJpaEntity toJpa(SupportEffortRecord d) {
        SupportEffortRecordJpaEntity e = new SupportEffortRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setResourceProfileId(d.resourceProfileId()); e.setEffortHours(d.effortHours());
        e.setEffortDate(d.effortDate()); e.setStatus(d.status());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportEffortRecord toDomain(SupportEffortRecordJpaEntity e) {
        return new SupportEffortRecord(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getResourceProfileId(),
                e.getEffortHours(), e.getEffortDate(), e.getStatus(), e.getCreatedAt());
    }
}
