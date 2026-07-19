package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;

import java.util.Optional;
import java.util.UUID;

public interface AiRecommendationPolicyRepository {
    AiRecommendationPolicy save(AiRecommendationPolicy policy);
    Optional<AiRecommendationPolicy> findById(UUID id);
    Optional<AiRecommendationPolicy> findByCode(String code);
    boolean existsByCode(String code);
}
