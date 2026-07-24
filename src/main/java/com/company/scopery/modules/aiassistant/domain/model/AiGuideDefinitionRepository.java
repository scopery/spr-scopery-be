package com.company.scopery.modules.aiassistant.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiGuideDefinitionRepository {

    Optional<AiGuideDefinition> findById(UUID id);

    Optional<AiGuideDefinition> findByPageCodeAndFieldCodeAndActionCodeAndLocaleAndStatus(
            String pageCode, String fieldCode, String actionCode, String locale, String status);

    List<AiGuideDefinition> findByPageCodeAndLocaleAndStatus(String pageCode, String locale, String status);

    List<AiGuideDefinition> findAll();

    AiGuideDefinition save(AiGuideDefinition guide);

    void retireById(UUID id);
}
