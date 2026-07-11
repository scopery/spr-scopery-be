package com.company.scopery.modules.aiagent.agent.infrastructure.persistence;

import com.company.scopery.modules.aiagent.agent.infrastructure.persistence.entity.AgentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataAgentJpaRepository
        extends JpaRepository<AgentJpaEntity, UUID>, JpaSpecificationExecutor<AgentJpaEntity> {

    boolean existsByCode(String code);

    List<AgentJpaEntity> findByStatus(String status);
}