package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrgTeamMemberJpaRepository
        extends JpaRepository<OrgTeamMemberJpaEntity, OrgTeamMemberKey> {

    boolean existsByIdTeamIdAndIdUserId(UUID teamId, UUID userId);

    Optional<OrgTeamMemberJpaEntity> findByIdTeamIdAndIdUserId(UUID teamId, UUID userId);

    void deleteByIdTeamIdAndIdUserId(UUID teamId, UUID userId);

    List<OrgTeamMemberJpaEntity> findAllByIdTeamId(UUID teamId);

    List<OrgTeamMemberJpaEntity> findAllByIdUserId(UUID userId);
}
