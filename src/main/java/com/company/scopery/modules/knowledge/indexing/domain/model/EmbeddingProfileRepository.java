package com.company.scopery.modules.knowledge.indexing.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface EmbeddingProfileRepository {
    Optional<EmbeddingProfile> findById(UUID id);
    Optional<EmbeddingProfile> findByCode(String code);
    Optional<EmbeddingProfile> findActiveDefault();
}
