package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiRecommendationPolicyJpaRepository
        extends JpaRepository<AiRecommendationPolicyJpaEntity, UUID> {

    Optional<AiRecommendationPolicyJpaEntity> findByCode(String code);

    boolean existsByCode(String code);
}
