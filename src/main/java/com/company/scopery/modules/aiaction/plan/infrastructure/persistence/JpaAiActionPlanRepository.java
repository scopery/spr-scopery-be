package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.infrastructure.mapper.AiActionPlanPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionPlanRepository implements AiActionPlanRepository {

    private final SpringDataAiActionPlanJpaRepository springDataRepository;
    private final AiActionPlanPersistenceMapper mapper;

    public JpaAiActionPlanRepository(SpringDataAiActionPlanJpaRepository springDataRepository,
                                      AiActionPlanPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionPlan save(AiActionPlan plan) {
        AiActionPlanJpaEntity entity = mapper.toJpaEntity(plan);
        AiActionPlanJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionPlan> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiActionPlan> findLatestByRequestId(UUID requestId) {
        return springDataRepository.findLatestByRequestId(requestId).map(mapper::toDomain);
    }
}
