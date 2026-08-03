package com.company.scopery.modules.traceability.aimapping.suggestion.domain.model;

import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MappingSuggestionRepository {

    MappingSuggestion save(MappingSuggestion suggestion);

    void saveAll(List<MappingSuggestion> suggestions);

    Optional<MappingSuggestion> findById(UUID id);

    List<MappingSuggestion> findAllById(List<UUID> ids);

    List<MappingSuggestion> findAcceptedByRunId(UUID runId);

    Page<MappingSuggestion> findByRunIdAndFilters(UUID runId,
                                                  String relationType,
                                                  String reviewStatus,
                                                  String confidenceBand,
                                                  String decision,
                                                  Boolean hasWarning,
                                                  UUID sourceId,
                                                  UUID targetId,
                                                  Pageable pageable);

    void updateReviewStatus(UUID id, SuggestionReviewStatus reviewStatus,
                            UUID reviewedBy, java.time.Instant reviewedAt);

    void bulkUpdateReviewStatus(List<UUID> ids, SuggestionReviewStatus reviewStatus,
                                UUID reviewedBy, java.time.Instant reviewedAt);
}
