package com.company.scopery.modules.airecommendation.application.port;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface Phase21SuggestionCompatibilityPort {
    Page<AiSuggestion> listForProject(UUID projectId, UUID workspaceId, Pageable pageable);
    Optional<AiSuggestion> getByLegacyId(UUID phase21Id, UUID actorId);
}
