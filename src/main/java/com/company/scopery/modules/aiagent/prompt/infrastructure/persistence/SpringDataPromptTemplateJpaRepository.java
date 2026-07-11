package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence;

import com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity.PromptTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataPromptTemplateJpaRepository
        extends JpaRepository<PromptTemplateJpaEntity, UUID>,
                JpaSpecificationExecutor<PromptTemplateJpaEntity> {

    boolean existsByAgentIdAndCode(UUID agentId, String code);
}