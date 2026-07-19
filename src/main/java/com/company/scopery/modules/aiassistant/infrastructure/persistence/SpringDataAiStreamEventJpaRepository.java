package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiStreamEventJpaRepository
        extends JpaRepository<AiStreamEventJpaEntity, UUID>, JpaSpecificationExecutor<AiStreamEventJpaEntity> {

    List<AiStreamEventJpaEntity> findByMessageIdAndSequenceGreaterThanOrderBySequenceAsc(UUID messageId, long afterSequence);
}
