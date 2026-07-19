package com.company.scopery.modules.quote.quote.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataQuoteJpaRepository extends JpaRepository<QuoteJpaEntity, UUID> {
    Optional<QuoteJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<QuoteJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
