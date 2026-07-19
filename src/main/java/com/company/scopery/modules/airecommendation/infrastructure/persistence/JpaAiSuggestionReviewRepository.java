package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionReviewRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionReviewPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAiSuggestionReviewRepository implements AiSuggestionReviewRepository {

    private final SpringDataAiSuggestionReviewJpaRepository springDataRepository;
    private final AiSuggestionReviewPersistenceMapper mapper;

    public JpaAiSuggestionReviewRepository(
            SpringDataAiSuggestionReviewJpaRepository springDataRepository,
            AiSuggestionReviewPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionReview save(AiSuggestionReview review) {
        AiSuggestionReviewJpaEntity entity = mapper.toJpaEntity(review);
        AiSuggestionReviewJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AiSuggestionReview> findBySuggestionId(UUID suggestionId) {
        return springDataRepository.findBySuggestionIdOrderByCreatedAtAsc(suggestionId)
                .stream().map(mapper::toDomain).toList();
    }
}
