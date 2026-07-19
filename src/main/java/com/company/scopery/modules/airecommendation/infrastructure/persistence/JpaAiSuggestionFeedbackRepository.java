package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionFeedback;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionFeedbackRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionFeedbackPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiSuggestionFeedbackRepository implements AiSuggestionFeedbackRepository {

    private final SpringDataAiSuggestionFeedbackJpaRepository springDataRepository;
    private final AiSuggestionFeedbackPersistenceMapper mapper;

    public JpaAiSuggestionFeedbackRepository(
            SpringDataAiSuggestionFeedbackJpaRepository springDataRepository,
            AiSuggestionFeedbackPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionFeedback save(AiSuggestionFeedback feedback) {
        AiSuggestionFeedbackJpaEntity entity = mapper.toJpaEntity(feedback);
        AiSuggestionFeedbackJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiSuggestionFeedback> findBySuggestionAndActor(UUID suggestionId, UUID actorId) {
        return springDataRepository.findBySuggestionIdAndActorId(suggestionId, actorId)
                .map(mapper::toDomain);
    }
}
