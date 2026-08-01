package com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence;

import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunication;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunicationRepository;
import com.company.scopery.modules.traceability.functioncomm.infrastructure.mapper.FunctionCommunicationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionCommunicationRepository implements FunctionCommunicationRepository {

    private final SpringDataFunctionCommunicationJpaRepository springData;
    private final FunctionCommunicationPersistenceMapper mapper;

    public JpaFunctionCommunicationRepository(SpringDataFunctionCommunicationJpaRepository springData,
                                              FunctionCommunicationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionCommunication save(FunctionCommunication link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public boolean existsByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId) {
        return springData.existsByIdFunctionIdAndIdCommunicationId(functionId, communicationId);
    }

    @Override
    public Optional<FunctionCommunication> findByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId) {
        return springData.findByIdFunctionIdAndIdCommunicationId(functionId, communicationId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionCommunication> findByFunctionId(UUID functionId) {
        return springData.findByIdFunctionId(functionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionCommunication> findByFunctionIdIn(Collection<UUID> functionIds) {
        if (functionIds == null || functionIds.isEmpty()) return List.of();
        return springData.findByIdFunctionIdIn(functionIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionCommunication> findByCommunicationId(UUID communicationId) {
        return springData.findByIdCommunicationId(communicationId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId) {
        springData.deleteByIdFunctionIdAndIdCommunicationId(functionId, communicationId);
    }
}
