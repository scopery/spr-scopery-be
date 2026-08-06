package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackAgentSessionJpaRepository extends JpaRepository<SpecPackAgentSessionJpaEntity, UUID> {

    List<SpecPackAgentSessionJpaEntity> findByProjectId(UUID projectId);

    Optional<SpecPackAgentSessionJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
