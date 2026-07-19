package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRecommendationPackDefinitionJpaRepository
        extends JpaRepository<RecommendationPackDefinitionJpaEntity, UUID> {

    @Query("SELECT e FROM RecommendationPackDefinitionJpaEntity e WHERE e.code = :code AND e.status = 'ACTIVE'")
    Optional<RecommendationPackDefinitionJpaEntity> findActiveByCode(@Param("code") String code);

    @Query("SELECT e FROM RecommendationPackDefinitionJpaEntity e WHERE e.status = 'ACTIVE' ORDER BY e.code")
    List<RecommendationPackDefinitionJpaEntity> findAllActive();

    boolean existsByCodeAndVersion(String code, int version);
}
