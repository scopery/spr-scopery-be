package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionItemRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiSuggestionItemRepository implements AiSuggestionItemRepository {

    private final SpringDataAiSuggestionItemJpaRepository springDataRepository;
    private final AiSuggestionItemPersistenceMapper mapper;

    public JpaAiSuggestionItemRepository(
            SpringDataAiSuggestionItemJpaRepository springDataRepository,
            AiSuggestionItemPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionItem save(AiSuggestionItem item) {
        AiSuggestionItemJpaEntity entity = mapper.toJpaEntity(item);
        AiSuggestionItemJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AiSuggestionItem> saveAll(List<AiSuggestionItem> items) {
        List<AiSuggestionItemJpaEntity> entities = items.stream().map(mapper::toJpaEntity).toList();
        List<AiSuggestionItemJpaEntity> saved = springDataRepository.saveAllAndFlush(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AiSuggestionItem> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AiSuggestionItem> findBySuggestionId(UUID suggestionId) {
        return springDataRepository.findBySuggestionIdOrderByOrdinal(suggestionId)
                .stream().map(mapper::toDomain).toList();
    }
}
