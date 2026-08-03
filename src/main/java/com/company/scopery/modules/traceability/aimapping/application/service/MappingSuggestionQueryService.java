package com.company.scopery.modules.traceability.aimapping.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.traceability.aimapping.application.query.ListMappingSuggestionsQuery;
import com.company.scopery.modules.traceability.aimapping.application.response.MappingRunResponse;
import com.company.scopery.modules.traceability.aimapping.application.response.MappingSuggestionResponse;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRunRepository;
import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MappingSuggestionQueryService {

    private final MappingRunRepository runRepository;
    private final MappingSuggestionRepository suggestionRepository;

    public MappingSuggestionQueryService(MappingRunRepository runRepository,
                                          MappingSuggestionRepository suggestionRepository) {
        this.runRepository = runRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @Transactional(readOnly = true)
    public MappingRunResponse getRun(UUID runId) {
        return runRepository.findById(runId)
                .map(MappingRunResponse::from)
                .orElseThrow(() -> AiMappingExceptions.runNotFound(runId));
    }

    @Transactional(readOnly = true)
    public PageResponse<MappingSuggestionResponse> listSuggestions(ListMappingSuggestionsQuery query) {
        Page<MappingSuggestionResponse> page = suggestionRepository.findByRunIdAndFilters(
                query.runId(),
                query.relationType(),
                query.reviewStatus(),
                query.confidenceBand(),
                query.decision(),
                query.hasWarning(),
                query.sourceId(),
                query.targetId(),
                query.pageable()
        ).map(MappingSuggestionResponse::from);
        return PageResponse.from(page);
    }
}
