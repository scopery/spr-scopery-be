package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataAiSuggestedQuestionJpaRepository
        extends JpaRepository<AiSuggestedQuestionJpaEntity, UUID>, JpaSpecificationExecutor<AiSuggestedQuestionJpaEntity> {
}
