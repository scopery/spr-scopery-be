package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseSupportingFunctionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaUseCaseSupportingFunctionRepository implements UseCaseSupportingFunctionRepository {

    private final SpringDataUseCaseSupFnJpaRepository springData;

    public JpaUseCaseSupportingFunctionRepository(SpringDataUseCaseSupFnJpaRepository springData) {
        this.springData = springData;
    }

    @Override
    public void link(UUID useCaseId, UUID functionId) {
        if (!exists(useCaseId, functionId)) {
            springData.saveAndFlush(new UseCaseSupFnJpaEntity(new UseCaseSupFnId(useCaseId, functionId)));
        }
    }

    @Override
    public void unlink(UUID useCaseId, UUID functionId) {
        springData.deleteById(new UseCaseSupFnId(useCaseId, functionId));
    }

    @Override
    public boolean exists(UUID useCaseId, UUID functionId) {
        return springData.existsById(new UseCaseSupFnId(useCaseId, functionId));
    }

    @Override
    public List<UUID> findFunctionIdsByUseCaseId(UUID useCaseId) {
        return springData.findFunctionIdsByUseCaseId(useCaseId);
    }

    @Override
    public List<UUID> findUseCaseIdsByFunctionId(UUID functionId) {
        return springData.findUseCaseIdsByFunctionId(functionId);
    }
}
