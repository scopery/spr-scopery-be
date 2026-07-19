package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiSuggestionRepository {
    AiSuggestion save(AiSuggestion suggestion);
    Optional<AiSuggestion> findById(UUID id);
    Optional<AiSuggestion> findActiveByWorkspaceAndDedupKey(UUID workspaceId, String dedupKey);
    Page<AiSuggestion> findByProjectWithFilters(UUID workspaceId, UUID projectId,
                                                List<SuggestionStatus> statuses,
                                                List<String> severities,
                                                String packCode,
                                                String suggestionType,
                                                String targetEntityType,
                                                boolean includeExpired,
                                                Pageable pageable);
    Page<AiSuggestion> findByEntity(UUID workspaceId, String entityType, UUID entityId, UUID projectId, Pageable pageable);
    List<AiSuggestion> findExpiredAndActive(OffsetDateTime before, int batchSize);
}
