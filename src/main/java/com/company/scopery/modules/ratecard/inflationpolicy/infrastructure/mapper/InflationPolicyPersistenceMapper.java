package com.company.scopery.modules.ratecard.inflationpolicy.infrastructure.mapper;

import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.*;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.infrastructure.persistence.InflationPolicyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InflationPolicyPersistenceMapper {
    public InflationPolicy toDomain(InflationPolicyJpaEntity e) {
        return new InflationPolicy(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                InflationPolicyScope.valueOf(e.getScope()), e.getOrganizationId(), e.getWorkspaceId(),
                e.getInflationPercent(), CompoundFrequency.valueOf(e.getCompoundFrequency()),
                e.getEffectiveFrom(), e.getEffectiveTo(), InflationPolicyStatus.valueOf(e.getStatus()),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public InflationPolicyJpaEntity toJpaEntity(InflationPolicy d) {
        InflationPolicyJpaEntity e = new InflationPolicyJpaEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setScope(d.scope().name()); e.setOrganizationId(d.organizationId()); e.setWorkspaceId(d.workspaceId());
        e.setInflationPercent(d.inflationPercent()); e.setCompoundFrequency(d.compoundFrequency().name());
        e.setEffectiveFrom(d.effectiveFrom()); e.setEffectiveTo(d.effectiveTo());
        e.setStatus(d.status().name()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        if (d.createdAt() != null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}
