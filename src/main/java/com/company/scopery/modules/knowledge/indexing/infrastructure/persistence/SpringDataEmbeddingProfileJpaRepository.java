package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmbeddingProfileJpaRepository extends JpaRepository<EmbeddingProfileJpaEntity, UUID> {
    Optional<EmbeddingProfileJpaEntity> findByCode(String code);
    Optional<EmbeddingProfileJpaEntity> findFirstByStatus(String status);
}
