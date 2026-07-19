package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiAgentToolBindingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiAgentToolBindingJpaRepository extends JpaRepository<AiAgentToolBindingJpaEntity, UUID> {

    boolean existsByAgentIdAndToolId(UUID agentId, UUID toolId);

    Optional<AiAgentToolBindingJpaEntity> findByAgentIdAndToolId(UUID agentId, UUID toolId);

    List<AiAgentToolBindingJpaEntity> findByToolId(UUID toolId);

    List<AiAgentToolBindingJpaEntity> findByAgentId(UUID agentId);
}
