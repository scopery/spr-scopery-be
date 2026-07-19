package com.company.scopery.modules.profitability.ratecard.infrastructure.mapper;

import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCard;
import com.company.scopery.modules.profitability.ratecard.infrastructure.persistence.ProfitRateCardJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitRateCardPersistenceMapper {

    public ProfitRateCard toDomain(ProfitRateCardJpaEntity e) {
        return new ProfitRateCard(
                e.getId(),
                e.getWorkspaceId(),
                e.getProjectId(),
                e.getRateCode(),
                e.getName(),
                e.getRateType(),
                e.getRoleName(),
                e.getTeamId(),
                e.getCurrency(),
                e.getAmountPerHour(),
                e.getAmountPerDay(),
                e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public ProfitRateCardJpaEntity toJpa(ProfitRateCard d) {
        ProfitRateCardJpaEntity e = new ProfitRateCardJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setRateCode(d.rateCode());
        e.setName(d.name());
        e.setRateType(d.rateType());
        e.setRoleName(d.roleName());
        e.setTeamId(d.teamId());
        e.setCurrency(d.currency());
        e.setAmountPerHour(d.amountPerHour());
        e.setAmountPerDay(d.amountPerDay());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
