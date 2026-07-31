package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseCondition;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseConditionRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCaseConditionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseConditionRepository implements UseCaseConditionRepository {

    private final SpringDataUseCaseConditionJpaRepository springData;
    private final UseCaseConditionPersistenceMapper mapper;

    public JpaUseCaseConditionRepository(SpringDataUseCaseConditionJpaRepository springData, UseCaseConditionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCaseCondition save(UseCaseCondition condition) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(condition)));
    }

    @Override
    public Optional<UseCaseCondition> findByIdAndUseCaseId(UUID id, UUID useCaseId) {
        return springData.findByIdAndUseCaseId(id, useCaseId).map(mapper::toDomain);
    }

    @Override
    public List<UseCaseCondition> findByUseCaseIdOrderByConditionTypeAndDisplayOrder(UUID useCaseId) {
        return springData.findByUseCaseIdOrderByConditionTypeAndDisplayOrder(useCaseId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndUseCaseId(UUID id, UUID useCaseId) {
        springData.deleteByIdAndUseCaseId(id, useCaseId);
    }
}
