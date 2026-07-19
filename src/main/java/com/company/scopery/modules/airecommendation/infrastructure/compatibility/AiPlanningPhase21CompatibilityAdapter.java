package com.company.scopery.modules.airecommendation.infrastructure.compatibility;

import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.airecommendation.application.compatibility.Phase21SuggestionCompatibilityMapper;
import com.company.scopery.modules.airecommendation.application.port.Phase21SuggestionCompatibilityPort;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AiPlanningPhase21CompatibilityAdapter implements Phase21SuggestionCompatibilityPort {

    private final AiPlanningSuggestionRepository suggestionRepository;
    private final Phase21SuggestionCompatibilityMapper mapper;

    public AiPlanningPhase21CompatibilityAdapter(AiPlanningSuggestionRepository suggestionRepository,
                                                  Phase21SuggestionCompatibilityMapper mapper) {
        this.suggestionRepository = suggestionRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<AiSuggestion> listForProject(UUID projectId, UUID workspaceId, Pageable pageable) {
        List<AiPlanningSuggestion> all = suggestionRepository.findByProjectId(projectId);
        List<AiSuggestion> mapped = all.stream()
                .filter(s -> workspaceId == null || workspaceId.equals(s.workspaceId()))
                .map(mapper::toCommon)
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), mapped.size());
        List<AiSuggestion> slice = (start >= mapped.size()) ? List.of() : mapped.subList(start, end);
        return new PageImpl<>(slice, pageable, mapped.size());
    }

    @Override
    public Optional<AiSuggestion> getByLegacyId(UUID phase21Id, UUID actorId) {
        return suggestionRepository.findById(phase21Id).map(mapper::toCommon);
    }
}
