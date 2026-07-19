package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRecommendationDetectorDefinitionJpaRepository
        extends JpaRepository<RecommendationDetectorDefinitionJpaEntity, UUID> {

    @Query("""
            SELECT e FROM RecommendationDetectorDefinitionJpaEntity e
            WHERE e.code = :code AND e.status = 'ACTIVE'
            """)
    Optional<RecommendationDetectorDefinitionJpaEntity> findActiveByCode(@Param("code") String code);

    @Query("""
            SELECT e FROM RecommendationDetectorDefinitionJpaEntity e
            WHERE e.packCode = :packCode AND e.status = 'ACTIVE'
            ORDER BY e.code
            """)
    List<RecommendationDetectorDefinitionJpaEntity> findAllActiveByPackCode(@Param("packCode") String packCode);

    boolean existsByCodeAndVersion(String code, int version);
}
