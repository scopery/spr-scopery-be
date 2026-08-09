package com.company.scopery.modules.elicitation.round.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataElicitationRoundJpaRepository
        extends JpaRepository<ElicitationRoundJpaEntity, UUID> {

    List<ElicitationRoundJpaEntity> findBySessionIdOrderByRoundNumberAsc(UUID sessionId);

    int countBySessionId(UUID sessionId);
}
