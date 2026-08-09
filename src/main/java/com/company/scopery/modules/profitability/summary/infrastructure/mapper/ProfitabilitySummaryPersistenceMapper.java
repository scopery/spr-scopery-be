package com.company.scopery.modules.profitability.summary.infrastructure.mapper;

import com.company.scopery.modules.profitability.summary.domain.enums.ProfitHealthStatus;
import com.company.scopery.modules.profitability.summary.domain.model.ProfitabilitySummary;
import com.company.scopery.modules.profitability.summary.infrastructure.persistence.ProfitabilitySummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitabilitySummaryPersistenceMapper {

    public ProfitabilitySummary toDomain(ProfitabilitySummaryJpaEntity e) {
        return new ProfitabilitySummary(
                e.getId(),
                e.getProjectId(),
                e.getCurrency(),
                e.getTotalRevenue(),
                e.getTotalCost(),
                e.getGrossMargin(),
                e.getGrossMarginPercent(),
                e.getProfitBeforeTax(),
                e.getPbtPercent(),
                ProfitHealthStatus.valueOf(e.getHealthStatus()),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public ProfitabilitySummaryJpaEntity toJpaEntity(ProfitabilitySummary d) {
        ProfitabilitySummaryJpaEntity e = new ProfitabilitySummaryJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setCurrency(d.currency());
        e.setTotalRevenue(d.totalRevenue());
        e.setTotalCost(d.totalCost());
        e.setGrossMargin(d.grossMargin());
        e.setGrossMarginPercent(d.grossMarginPercent());
        e.setProfitBeforeTax(d.profitBeforeTax());
        e.setPbtPercent(d.pbtPercent());
        e.setHealthStatus(d.healthStatus().name());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
