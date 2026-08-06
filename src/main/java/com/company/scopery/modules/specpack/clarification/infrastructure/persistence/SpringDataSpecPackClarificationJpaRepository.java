package com.company.scopery.modules.specpack.clarification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSpecPackClarificationJpaRepository extends JpaRepository<SpecPackClarificationJpaEntity, UUID> {

    List<SpecPackClarificationJpaEntity> findBySessionId(UUID sessionId);

    List<SpecPackClarificationJpaEntity> findBySessionIdAndStatus(UUID sessionId, String status);

    long countBySessionIdAndPriorityAndStatus(UUID sessionId, String priority, String status);
}
