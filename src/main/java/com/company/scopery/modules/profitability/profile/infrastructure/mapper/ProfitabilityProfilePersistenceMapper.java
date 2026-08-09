package com.company.scopery.modules.profitability.profile.infrastructure.mapper;

import com.company.scopery.modules.profitability.profile.domain.enums.ProfitabilityProfileStatus;
import com.company.scopery.modules.profitability.profile.domain.model.ProfitabilityProfile;
import com.company.scopery.modules.profitability.profile.infrastructure.persistence.ProfitabilityProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitabilityProfilePersistenceMapper {

    public ProfitabilityProfile toDomain(ProfitabilityProfileJpaEntity e) {
        return new ProfitabilityProfile(
                e.getId(),
                e.getProjectId(),
                e.getWorkspaceId(),
                e.getCurrency(),
                ProfitabilityProfileStatus.valueOf(e.getStatus()),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public ProfitabilityProfileJpaEntity toJpaEntity(ProfitabilityProfile d) {
        ProfitabilityProfileJpaEntity e = new ProfitabilityProfileJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setCurrency(d.currency());
        e.setStatus(d.status().name());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
