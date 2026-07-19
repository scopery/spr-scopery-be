package com.company.scopery.modules.integrationhub.deadletter.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDeadLetterEventJpaRepository extends JpaRepository<DeadLetterEventJpaEntity, UUID> {
    List<DeadLetterEventJpaEntity> findByWorkspaceId(UUID workspaceId);
}
