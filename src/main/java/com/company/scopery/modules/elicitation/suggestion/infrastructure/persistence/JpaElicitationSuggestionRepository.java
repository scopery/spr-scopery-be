package com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence;

import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import com.company.scopery.modules.elicitation.suggestion.infrastructure.mapper.ElicitationSuggestionPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaElicitationSuggestionRepository implements ElicitationSuggestionRepository {

    private final SpringDataElicitationSuggestionJpaRepository suggestionRepo;
    private final SpringDataElicitationSuggestionItemJpaRepository itemRepo;
    private final ElicitationSuggestionPersistenceMapper mapper;

    public JpaElicitationSuggestionRepository(SpringDataElicitationSuggestionJpaRepository suggestionRepo,
                                               SpringDataElicitationSuggestionItemJpaRepository itemRepo,
                                               ElicitationSuggestionPersistenceMapper mapper) {
        this.suggestionRepo = suggestionRepo;
        this.itemRepo = itemRepo;
        this.mapper = mapper;
    }

    @Override
    public ElicitationSuggestion save(ElicitationSuggestion suggestion) {
        ElicitationSuggestionJpaEntity entity = mapper.toJpaEntity(suggestion);
        ElicitationSuggestionJpaEntity saved = suggestionRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ElicitationSuggestion> findById(UUID id) {
        return suggestionRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ElicitationSuggestion> findByRoundId(UUID roundId) {
        return suggestionRepo.findByRoundId(roundId).map(mapper::toDomain);
    }

    @Override
    public ElicitationSuggestionItem saveItem(ElicitationSuggestionItem item) {
        ElicitationSuggestionItemJpaEntity entity = mapper.toItemJpaEntity(item);
        ElicitationSuggestionItemJpaEntity saved = itemRepo.saveAndFlush(entity);
        return mapper.toItemDomain(saved);
    }

    @Override
    public List<ElicitationSuggestionItem> saveAllItems(List<ElicitationSuggestionItem> items) {
        List<ElicitationSuggestionItemJpaEntity> entities = items.stream().map(mapper::toItemJpaEntity).toList();
        return itemRepo.saveAllAndFlush(entities).stream().map(mapper::toItemDomain).toList();
    }

    @Override
    @Transactional
    public void deleteWithItemsByRoundId(UUID roundId) {
        suggestionRepo.findByRoundId(roundId).ifPresent(existing -> {
            itemRepo.deleteBySuggestionId(existing.getId());
            suggestionRepo.deleteById(existing.getId());
        });
    }

    @Override
    public Optional<ElicitationSuggestionItem> findItemById(UUID itemId) {
        return itemRepo.findById(itemId).map(mapper::toItemDomain);
    }

    @Override
    public List<ElicitationSuggestionItem> findAllItemsBySuggestionId(UUID suggestionId) {
        return itemRepo.findBySuggestionIdOrderBySequenceAsc(suggestionId).stream().map(mapper::toItemDomain).toList();
    }
}
