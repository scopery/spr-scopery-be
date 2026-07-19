package com.company.scopery.modules.ratecard.ratecard.infrastructure.mapper;

import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.infrastructure.persistence.RateCardJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RateCardPersistenceMapper {
    public RateCard toDomain(RateCardJpaEntity e) {
        return new RateCard(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                RateCardScope.valueOf(e.getScope()), e.getOrganizationId(), e.getWorkspaceId(),
                e.getClientId(), e.getProjectId(), e.getDefaultCurrencyCode(), e.isDefault(),
                RateCardStatus.valueOf(e.getStatus()), e.getCurrentVersionId(), e.isBuiltIn(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public RateCardJpaEntity toJpaEntity(RateCard d) {
        RateCardJpaEntity e = new RateCardJpaEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setScope(d.scope().name()); e.setOrganizationId(d.organizationId()); e.setWorkspaceId(d.workspaceId());
        e.setClientId(d.clientId()); e.setProjectId(d.projectId());
        e.setDefaultCurrencyCode(d.defaultCurrencyCode()); e.setDefault(d.isDefault());
        e.setStatus(d.status().name()); e.setCurrentVersionId(d.currentVersionId());
        e.setBuiltIn(d.builtIn()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
