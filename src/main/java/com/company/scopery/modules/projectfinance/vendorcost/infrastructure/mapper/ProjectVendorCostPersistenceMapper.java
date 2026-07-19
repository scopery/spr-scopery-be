package com.company.scopery.modules.projectfinance.vendorcost.infrastructure.mapper;

import com.company.scopery.modules.projectfinance.vendorcost.domain.enums.VendorCostStatus;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.infrastructure.persistence.ProjectVendorCostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectVendorCostPersistenceMapper {
    public ProjectVendorCost toDomain(ProjectVendorCostJpaEntity e) {
        return new ProjectVendorCost(
                e.getId(), e.getFinanceScenarioId(), e.getProjectId(), e.getProjectPhaseId(),
                e.getVendorName(), e.getExternalPartyId(), e.getDescription(), e.getAmount(),
                e.getCurrencyCode(), VendorCostStatus.valueOf(e.getStatus()),
                e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectVendorCostJpaEntity toJpaEntity(ProjectVendorCost d) {
        ProjectVendorCostJpaEntity e = new ProjectVendorCostJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectId(d.projectId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setVendorName(d.vendorName());
        e.setExternalPartyId(d.externalPartyId());
        e.setDescription(d.description());
        e.setAmount(d.amount());
        e.setCurrencyCode(d.currencyCode());
        e.setStatus(d.status().name());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
