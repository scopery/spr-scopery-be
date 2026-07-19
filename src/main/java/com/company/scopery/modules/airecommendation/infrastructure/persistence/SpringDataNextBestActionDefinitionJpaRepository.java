package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringDataNextBestActionDefinitionJpaRepository
        extends JpaRepository<NextBestActionDefinitionJpaEntity, UUID> {

    @Query("SELECT e FROM NextBestActionDefinitionJpaEntity e WHERE e.status = 'ACTIVE' ORDER BY e.code")
    List<NextBestActionDefinitionJpaEntity> findAllActive();

    boolean existsByCodeAndVersion(String code, int version);
}
