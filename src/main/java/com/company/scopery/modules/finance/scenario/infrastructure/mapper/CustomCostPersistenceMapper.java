package com.company.scopery.modules.finance.scenario.infrastructure.mapper;

import com.company.scopery.modules.finance.scenario.domain.enums.CostLineStatus;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;
import com.company.scopery.modules.finance.scenario.infrastructure.persistence.CustomCostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomCostPersistenceMapper {

    public CustomCost toDomain(CustomCostJpaEntity e) {
        return new CustomCost(
                e.getId(),
                e.getFinanceScenarioId(),
                e.getProjectPhaseId(),
                e.getCategory(),
                e.getName(),
                e.getDescription(),
                e.getAmount(),
                e.getCurrencyCode(),
                e.getCostDate(),
                e.getStatus() != null ? CostLineStatus.valueOf(e.getStatus()) : CostLineStatus.ACTIVE,
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public CustomCostJpaEntity toJpaEntity(CustomCost d) {
        CustomCostJpaEntity e = new CustomCostJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setCategory(d.category());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setAmount(d.amount());
        e.setCurrencyCode(d.currencyCode());
        e.setCostDate(d.costDate());
        e.setStatus(d.status() != null ? d.status().name() : CostLineStatus.ACTIVE.name());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
