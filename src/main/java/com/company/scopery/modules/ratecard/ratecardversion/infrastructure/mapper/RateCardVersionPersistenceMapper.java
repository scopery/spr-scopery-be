package com.company.scopery.modules.ratecard.ratecardversion.infrastructure.mapper;

import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import com.company.scopery.modules.ratecard.ratecardversion.infrastructure.persistence.RateCardVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RateCardVersionPersistenceMapper {
    public RateCardVersion toDomain(RateCardVersionJpaEntity e) {
        return new RateCardVersion(e.getId(), e.getRateCardId(), e.getVersionNumber(), e.getName(), e.getDescription(),
                e.getEffectiveFrom(), e.getEffectiveTo(), RateCardVersionStatus.valueOf(e.getStatus()),
                e.getPublishedAt(), e.getPublishedBy(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() != null ? e.getVersion() : 0, e.getCreatedAt(), e.getUpdatedAt());
    }
    public RateCardVersionJpaEntity toJpaEntity(RateCardVersion d) {
        RateCardVersionJpaEntity e = new RateCardVersionJpaEntity();
        e.setId(d.id()); e.setRateCardId(d.rateCardId()); e.setVersionNumber(d.versionNumber());
        e.setName(d.name()); e.setDescription(d.description());
        e.setEffectiveFrom(d.effectiveFrom()); e.setEffectiveTo(d.effectiveTo());
        e.setStatus(d.status().name()); e.setPublishedAt(d.publishedAt()); e.setPublishedBy(d.publishedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
