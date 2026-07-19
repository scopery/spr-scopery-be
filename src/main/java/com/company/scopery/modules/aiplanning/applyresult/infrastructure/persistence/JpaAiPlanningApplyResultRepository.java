package com.company.scopery.modules.aiplanning.applyresult.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResult;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResultRepository;
import com.company.scopery.modules.aiplanning.applyresult.infrastructure.mapper.AiPlanningApplyResultPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaAiPlanningApplyResultRepository implements AiPlanningApplyResultRepository {
    private final SpringDataAiPlanningApplyResultJpaRepository springData;
    private final AiPlanningApplyResultPersistenceMapper mapper;
    public JpaAiPlanningApplyResultRepository(SpringDataAiPlanningApplyResultJpaRepository springData,
                                              AiPlanningApplyResultPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public AiPlanningApplyResult save(AiPlanningApplyResult result) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(result)));
    }
    @Override public List<AiPlanningApplyResult> findBySuggestionId(UUID suggestionId) {
        return springData.findBySuggestionIdOrderByCreatedAtAsc(suggestionId).stream().map(mapper::toDomain).toList();
    }
}
