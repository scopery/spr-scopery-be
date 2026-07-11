package com.company.scopery.modules.workspace.orgteam.infrastructure.mapper;

import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;
import com.company.scopery.modules.workspace.orgteam.infrastructure.persistence.OrgTeamJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrgTeamPersistenceMapper {

    public OrgTeam toDomain(OrgTeamJpaEntity entity) {
        return new OrgTeam(
                entity.getId(),
                entity.getOrganizationId(),
                new OrgTeamCode(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                OrgTeamStatus.valueOf(entity.getStatus()),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public OrgTeamJpaEntity toJpaEntity(OrgTeam domain) {
        OrgTeamJpaEntity entity = new OrgTeamJpaEntity();
        entity.setId(domain.id());
        entity.setOrganizationId(domain.organizationId());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
