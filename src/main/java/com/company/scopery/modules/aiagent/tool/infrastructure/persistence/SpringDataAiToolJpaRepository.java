package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiToolJpaRepository
        extends JpaRepository<AiToolJpaEntity, UUID>, JpaSpecificationExecutor<AiToolJpaEntity> {

    boolean existsByCode(String code);

    Optional<AiToolJpaEntity> findByCode(String code);
}
