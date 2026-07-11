package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataOrgTeamJpaRepository
        extends JpaRepository<OrgTeamJpaEntity, UUID>, JpaSpecificationExecutor<OrgTeamJpaEntity> {

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
