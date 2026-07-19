package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendationDetectorDefinitionRepository {
    RecommendationDetectorDefinition save(RecommendationDetectorDefinition detector);
    Optional<RecommendationDetectorDefinition> findById(UUID id);
    Optional<RecommendationDetectorDefinition> findActiveByCode(String code);
    List<RecommendationDetectorDefinition> findAllActiveByPackCode(String packCode);
    boolean existsByCodeAndVersion(String code, int version);
}
