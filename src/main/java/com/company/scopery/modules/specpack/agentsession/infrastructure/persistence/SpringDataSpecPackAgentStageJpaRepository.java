package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackAgentStageJpaRepository extends JpaRepository<SpecPackAgentStageJpaEntity, UUID> {

    List<SpecPackAgentStageJpaEntity> findBySessionId(UUID sessionId);

    Optional<SpecPackAgentStageJpaEntity> findBySessionIdAndStageCode(UUID sessionId, String stageCode);
}
