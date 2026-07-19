package com.company.scopery.modules.projectfinance.customcost.infrastructure.mapper;

import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostCategory;
import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostStatus;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.customcost.infrastructure.persistence.ProjectCustomCostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectCustomCostPersistenceMapper {
    public ProjectCustomCost toDomain(ProjectCustomCostJpaEntity e) {
        return new ProjectCustomCost(
                e.getId(), e.getFinanceScenarioId(), e.getProjectId(), e.getProjectPhaseId(),
                CustomCostCategory.valueOf(e.getCategory()), e.getName(), e.getDescription(),
                e.getAmount(), e.getCurrencyCode(), e.getCostDate(),
                CustomCostStatus.valueOf(e.getStatus()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectCustomCostJpaEntity toJpaEntity(ProjectCustomCost d) {
        ProjectCustomCostJpaEntity e = new ProjectCustomCostJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectId(d.projectId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setCategory(d.category().name());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setAmount(d.amount());
        e.setCurrencyCode(d.currencyCode());
        e.setCostDate(d.costDate());
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
