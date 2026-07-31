package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterion;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterionRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCaseAcceptanceCriterionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseAcceptanceCriterionRepository implements UseCaseAcceptanceCriterionRepository {

    private final SpringDataUseCaseAcceptanceCriterionJpaRepository springData;
    private final UseCaseAcceptanceCriterionPersistenceMapper mapper;

    public JpaUseCaseAcceptanceCriterionRepository(SpringDataUseCaseAcceptanceCriterionJpaRepository springData, UseCaseAcceptanceCriterionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCaseAcceptanceCriterion save(UseCaseAcceptanceCriterion criterion) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(criterion)));
    }

    @Override
    public Optional<UseCaseAcceptanceCriterion> findByIdAndUseCaseId(UUID id, UUID useCaseId) {
        return springData.findByIdAndUseCaseId(id, useCaseId).map(mapper::toDomain);
    }

    @Override
    public List<UseCaseAcceptanceCriterion> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId) {
        return springData.findByUseCaseIdOrderByDisplayOrderAsc(useCaseId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndUseCaseId(UUID id, UUID useCaseId) {
        springData.deleteByIdAndUseCaseId(id, useCaseId);
    }
}
