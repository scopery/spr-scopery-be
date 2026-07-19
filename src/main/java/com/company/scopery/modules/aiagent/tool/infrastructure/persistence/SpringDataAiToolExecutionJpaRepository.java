package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataAiToolExecutionJpaRepository
        extends JpaRepository<AiToolExecutionJpaEntity, UUID>, JpaSpecificationExecutor<AiToolExecutionJpaEntity> {
}
