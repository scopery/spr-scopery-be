package com.company.scopery.modules.workspace.orgmember.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrgMemberJpaRepository
        extends JpaRepository<OrgMemberJpaEntity, UUID>,
                JpaSpecificationExecutor<OrgMemberJpaEntity> {

    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    Optional<OrgMemberJpaEntity> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    boolean existsByOrganizationIdAndUserIdAndStatus(UUID organizationId, UUID userId, String status);

    List<OrgMemberJpaEntity> findAllByUserId(UUID userId);
}
