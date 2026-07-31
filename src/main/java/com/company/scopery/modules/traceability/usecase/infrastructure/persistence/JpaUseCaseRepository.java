package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.company.scopery.modules.traceability.usecase.infrastructure.mapper.UseCasePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUseCaseRepository implements UseCaseRepository {

    private final SpringDataUseCaseJpaRepository springData;
    private final UseCasePersistenceMapper mapper;

    public JpaUseCaseRepository(SpringDataUseCaseJpaRepository springData, UseCasePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public UseCase save(UseCase useCase) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(useCase)));
    }

    @Override
    public Optional<UseCase> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndKey(UUID projectId, String key) {
        return springData.existsByProjectIdAndKey(projectId, key);
    }

    @Override
    public List<UseCase> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<UseCase> findByPrimaryFunctionId(UUID functionId) {
        return springData.findByPrimaryFunctionId(functionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndProjectId(UUID id, UUID projectId) {
        springData.deleteByIdAndProjectId(id, projectId);
    }
}
