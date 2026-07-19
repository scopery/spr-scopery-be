package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.NextBestActionDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NextBestActionDefinitionRepository {
    NextBestActionDefinition save(NextBestActionDefinition nba);
    Optional<NextBestActionDefinition> findById(UUID id);
    List<NextBestActionDefinition> findAllActive();
    boolean existsByCodeAndVersion(String code, int version);
}
