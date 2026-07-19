package com.company.scopery.modules.aiplanning.reviewaction.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewAction;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewActionRepository;
import com.company.scopery.modules.aiplanning.reviewaction.infrastructure.mapper.AiPlanningReviewActionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaAiPlanningReviewActionRepository implements AiPlanningReviewActionRepository {
    private final SpringDataAiPlanningReviewActionJpaRepository springData;
    private final AiPlanningReviewActionPersistenceMapper mapper;
    public JpaAiPlanningReviewActionRepository(SpringDataAiPlanningReviewActionJpaRepository springData,
                                               AiPlanningReviewActionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public AiPlanningReviewAction save(AiPlanningReviewAction action) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(action)));
    }
    @Override public List<AiPlanningReviewAction> findBySuggestionId(UUID suggestionId) {
        return springData.findBySuggestionIdOrderByCreatedAtAsc(suggestionId).stream().map(mapper::toDomain).toList();
    }
}
