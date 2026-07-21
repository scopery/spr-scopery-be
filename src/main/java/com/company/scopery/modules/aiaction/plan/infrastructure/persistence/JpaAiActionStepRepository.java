package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.plan.infrastructure.mapper.AiActionStepPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionStepRepository implements AiActionStepRepository {

    private final SpringDataAiActionStepJpaRepository springDataRepository;
    private final AiActionStepPersistenceMapper mapper;

    public JpaAiActionStepRepository(SpringDataAiActionStepJpaRepository springDataRepository,
                                      AiActionStepPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionStep save(AiActionStep step) {
        AiActionStepJpaEntity entity = mapper.toJpaEntity(step);
        AiActionStepJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AiActionStep> saveAll(List<AiActionStep> steps) {
        List<AiActionStepJpaEntity> entities = steps.stream().map(mapper::toJpaEntity).toList();
        List<AiActionStepJpaEntity> saved = springDataRepository.saveAllAndFlush(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiActionStep> findByPlanIdOrderByOrdinal(UUID planId) {
        return springDataRepository.findByPlanIdOrderByOrdinal(planId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AiActionStep> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
