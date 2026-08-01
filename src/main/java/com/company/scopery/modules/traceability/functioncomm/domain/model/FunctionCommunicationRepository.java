package com.company.scopery.modules.traceability.functioncomm.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionCommunicationRepository {
    FunctionCommunication save(FunctionCommunication link);
    boolean existsByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId);
    Optional<FunctionCommunication> findByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId);
    List<FunctionCommunication> findByFunctionId(UUID functionId);
    List<FunctionCommunication> findByFunctionIdIn(Collection<UUID> functionIds);
    List<FunctionCommunication> findByCommunicationId(UUID communicationId);
    void deleteByFunctionIdAndCommunicationId(UUID functionId, UUID communicationId);
}
