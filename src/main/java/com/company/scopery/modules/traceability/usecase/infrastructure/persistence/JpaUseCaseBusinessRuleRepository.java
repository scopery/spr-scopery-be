package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRule;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRuleRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCaseBusinessRulePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseBusinessRuleRepository implements UseCaseBusinessRuleRepository {

    private final SpringDataUseCaseBusinessRuleJpaRepository springData;
    private final UseCaseBusinessRulePersistenceMapper mapper;

    public JpaUseCaseBusinessRuleRepository(SpringDataUseCaseBusinessRuleJpaRepository springData, UseCaseBusinessRulePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCaseBusinessRule save(UseCaseBusinessRule rule) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(rule)));
    }

    @Override
    public Optional<UseCaseBusinessRule> findByIdAndUseCaseId(UUID id, UUID useCaseId) {
        return springData.findByIdAndUseCaseId(id, useCaseId).map(mapper::toDomain);
    }

    @Override
    public List<UseCaseBusinessRule> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId) {
        return springData.findByUseCaseIdOrderByDisplayOrderAsc(useCaseId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndUseCaseId(UUID id, UUID useCaseId) {
        springData.deleteByIdAndUseCaseId(id, useCaseId);
    }
}
