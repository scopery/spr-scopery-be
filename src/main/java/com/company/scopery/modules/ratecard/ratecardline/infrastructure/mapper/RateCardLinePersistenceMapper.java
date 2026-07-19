package com.company.scopery.modules.ratecard.ratecardline.infrastructure.mapper;

import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.infrastructure.persistence.RateCardLineJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RateCardLinePersistenceMapper {
    public RateCardLine toDomain(RateCardLineJpaEntity e) {
        return new RateCardLine(e.getId(), e.getRateCardVersionId(), e.getCostRoleId(), e.getSeniorityLevel(),
                e.getLocationCode(), e.getCurrencyCode(), e.getCostRatePerHour(), e.getBillingRatePerHour(),
                e.getNotes(), e.getVersion() != null ? e.getVersion() : 0, e.getCreatedAt(), e.getUpdatedAt());
    }
    public RateCardLineJpaEntity toJpaEntity(RateCardLine d) {
        RateCardLineJpaEntity e = new RateCardLineJpaEntity();
        e.setId(d.id()); e.setRateCardVersionId(d.rateCardVersionId()); e.setCostRoleId(d.costRoleId());
        e.setSeniorityLevel(d.seniorityLevel()); e.setLocationCode(d.locationCode());
        e.setCurrencyCode(d.currencyCode()); e.setCostRatePerHour(d.costRatePerHour());
        e.setBillingRatePerHour(d.billingRatePerHour()); e.setNotes(d.notes()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
