package com.company.scopery.modules.traceability.functionapi.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionApiRepository {
    FunctionApi save(FunctionApi link);
    boolean existsByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId);
    Optional<FunctionApi> findByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId);
    List<FunctionApi> findByFunctionId(UUID functionId);
    List<FunctionApi> findByFunctionIdIn(Collection<UUID> functionIds);
    List<FunctionApi> findByApiEndpointId(UUID apiEndpointId);
    void deleteByFunctionIdAndApiEndpointId(UUID functionId, UUID apiEndpointId);
}
