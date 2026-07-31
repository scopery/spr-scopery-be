package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStep;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCaseFlowStepPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseFlowStepRepository implements UseCaseFlowStepRepository {

    private final SpringDataUseCaseFlowStepJpaRepository springData;
    private final UseCaseFlowStepPersistenceMapper mapper;

    public JpaUseCaseFlowStepRepository(SpringDataUseCaseFlowStepJpaRepository springData, UseCaseFlowStepPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCaseFlowStep save(UseCaseFlowStep step) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(step)));
    }

    @Override
    public List<UseCaseFlowStep> saveAll(List<UseCaseFlowStep> steps) {
        List<UseCaseFlowStepJpaEntity> entities = steps.stream().map(mapper::toJpaEntity).toList();
        return springData.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<UseCaseFlowStep> findByIdAndFlowId(UUID id, UUID flowId) {
        return springData.findByIdAndFlowId(id, flowId).map(mapper::toDomain);
    }

    @Override
    public List<UseCaseFlowStep> findByFlowIdOrderByDisplayOrder(UUID flowId) {
        return springData.findByFlowIdOrderByDisplayOrderAsc(flowId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndFlowId(UUID id, UUID flowId) {
        springData.deleteByIdAndFlowId(id, flowId);
    }
}
