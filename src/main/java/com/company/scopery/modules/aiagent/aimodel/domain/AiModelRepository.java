package com.company.scopery.modules.aiagent.aimodel.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AiModelRepository {

    AiModel save(AiModel model);

    Optional<AiModel> findById(UUID id);

    boolean existsByProviderIdAndCode(UUID providerId, AiModelCode code);

    Page<AiModel> findAll(UUID providerId, String keyword, AiModelStatus status, AiModelType type, Pageable pageable);
}