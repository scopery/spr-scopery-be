package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.RecommendationPackDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendationPackDefinitionRepository {
    RecommendationPackDefinition save(RecommendationPackDefinition pack);
    Optional<RecommendationPackDefinition> findById(UUID id);
    Optional<RecommendationPackDefinition> findActiveByCode(String code);
    List<RecommendationPackDefinition> findAllActive();
    boolean existsByCodeAndVersion(String code, int version);
}
