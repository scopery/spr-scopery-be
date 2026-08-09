package com.company.scopery.modules.finance.scenario.infrastructure.mapper;

import com.company.scopery.modules.finance.scenario.domain.enums.CostLineStatus;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;
import com.company.scopery.modules.finance.scenario.infrastructure.persistence.VendorCostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class VendorCostPersistenceMapper {

    public VendorCost toDomain(VendorCostJpaEntity e) {
        return new VendorCost(
                e.getId(),
                e.getFinanceScenarioId(),
                e.getProjectPhaseId(),
                e.getVendorName(),
                e.getDescription(),
                e.getAmount(),
                e.getCurrencyCode(),
                e.getStatus() != null ? CostLineStatus.valueOf(e.getStatus()) : CostLineStatus.ACTIVE,
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public VendorCostJpaEntity toJpaEntity(VendorCost d) {
        VendorCostJpaEntity e = new VendorCostJpaEntity();
        e.setId(d.id());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setVendorName(d.vendorName());
        e.setDescription(d.description());
        e.setAmount(d.amount());
        e.setCurrencyCode(d.currencyCode());
        e.setStatus(d.status() != null ? d.status().name() : CostLineStatus.ACTIVE.name());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
