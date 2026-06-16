package com.company.scopery.modules.workspace.organization.infrastructure.mapper;

import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.infrastructure.persistence.OrganizationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationPersistenceMapper {

    public Organization toDomain(OrganizationJpaEntity entity) {
        return new Organization(
                entity.getId(),
                OrganizationCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerUserId(),
                OrganizationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public OrganizationJpaEntity toJpaEntity(Organization domain) {
        OrganizationJpaEntity entity = new OrganizationJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt()); // required for Spring Data merge()
        return entity;
    }
}
