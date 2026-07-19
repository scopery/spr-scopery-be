package com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.mapper.AiPlanningSuggestionItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiPlanningSuggestionItemRepository implements AiPlanningSuggestionItemRepository {
    private final SpringDataAiPlanningSuggestionItemJpaRepository springData;
    private final AiPlanningSuggestionItemPersistenceMapper mapper;

    public JpaAiPlanningSuggestionItemRepository(SpringDataAiPlanningSuggestionItemJpaRepository springData,
                                                 AiPlanningSuggestionItemPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override public AiPlanningSuggestionItem save(AiPlanningSuggestionItem item) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item)));
    }
    @Override public Optional<AiPlanningSuggestionItem> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
    @Override public Optional<AiPlanningSuggestionItem> findByIdAndSuggestionId(UUID id, UUID suggestionId) {
        return springData.findByIdAndSuggestionId(id, suggestionId).map(mapper::toDomain);
    }
    @Override public List<AiPlanningSuggestionItem> findBySuggestionId(UUID suggestionId) {
        return springData.findBySuggestionIdOrderByCreatedAtAsc(suggestionId).stream().map(mapper::toDomain).toList();
    }
}
