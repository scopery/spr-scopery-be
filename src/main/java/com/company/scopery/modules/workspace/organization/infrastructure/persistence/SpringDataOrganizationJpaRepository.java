package com.company.scopery.modules.workspace.organization.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataOrganizationJpaRepository
        extends JpaRepository<OrganizationJpaEntity, UUID>,
                JpaSpecificationExecutor<OrganizationJpaEntity> {

    boolean existsByCode(String code);
}
