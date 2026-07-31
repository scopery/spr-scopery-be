package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlow;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCaseFlowPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseFlowRepository implements UseCaseFlowRepository {

    private final SpringDataUseCaseFlowJpaRepository springData;
    private final UseCaseFlowPersistenceMapper mapper;

    public JpaUseCaseFlowRepository(SpringDataUseCaseFlowJpaRepository springData, UseCaseFlowPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCaseFlow save(UseCaseFlow flow) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(flow)));
    }

    @Override
    public Optional<UseCaseFlow> findByIdAndUseCaseId(UUID id, UUID useCaseId) {
        return springData.findByIdAndUseCaseId(id, useCaseId).map(mapper::toDomain);
    }

    @Override
    public List<UseCaseFlow> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId) {
        return springData.findByUseCaseIdOrderByDisplayOrderAsc(useCaseId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByUseCaseIdAndFlowType(UUID useCaseId, UseCaseFlowType flowType) {
        return springData.existsByUseCaseIdAndFlowType(useCaseId, flowType.name());
    }

    @Override
    public void deleteByIdAndUseCaseId(UUID id, UUID useCaseId) {
        springData.deleteByIdAndUseCaseId(id, useCaseId);
    }
}
