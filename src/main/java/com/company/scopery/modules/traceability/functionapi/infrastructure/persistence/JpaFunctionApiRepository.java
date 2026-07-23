package com.company.scopery.modules.traceability.functionapi.infrastructure.persistence;

import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.functionapi.infrastructure.mapper.FunctionApiPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionApiRepository implements FunctionApiRepository {

    private final SpringDataFunctionApiJpaRepository springData;
    private final FunctionApiPersistenceMapper mapper;

    public JpaFunctionApiRepository(SpringDataFunctionApiJpaRepository springData,
                                    FunctionApiPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionApi save(FunctionApi link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public boolean existsByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId) {
        return springData.existsByIdFunctionIdAndIdApiEndpointId(functionId, apiEndpointId);
    }

    @Override
    public Optional<FunctionApi> findByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId) {
        return springData.findByIdFunctionIdAndIdApiEndpointId(functionId, apiEndpointId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionApi> findByFunctionId(UUID functionId) {
        return springData.findByIdFunctionId(functionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionApi> findByFunctionIdIn(Collection<UUID> functionIds) {
        return springData.findByIdFunctionIdIn(functionIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionApi> findByApiEndpointId(UUID apiEndpointId) {
        return springData.findByIdApiEndpointId(apiEndpointId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId) {
        springData.deleteByIdFunctionIdAndIdApiEndpointId(functionId, apiEndpointId);
    }
}
