package com.company.scopery.modules.servicesupport.warranty.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverage;
import com.company.scopery.modules.servicesupport.warranty.infrastructure.persistence.WarrantyCoverageJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WarrantyCoveragePersistenceMapper {
    public WarrantyCoverageJpaEntity toJpa(WarrantyCoverage d) {
        WarrantyCoverageJpaEntity e = new WarrantyCoverageJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setServiceProfileId(d.serviceProfileId()); e.setStartDate(d.startDate());
        e.setEndDate(d.endDate()); e.setStatus(d.status());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public WarrantyCoverage toDomain(WarrantyCoverageJpaEntity e) {
        return new WarrantyCoverage(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getServiceProfileId(),
                e.getStartDate(), e.getEndDate(), e.getStatus(), e.getCreatedAt());
    }
}
