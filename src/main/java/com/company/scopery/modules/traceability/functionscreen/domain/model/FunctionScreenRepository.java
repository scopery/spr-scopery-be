package com.company.scopery.modules.traceability.functionscreen.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionScreenRepository {
    FunctionScreen save(FunctionScreen link);
    boolean existsByFunctionIdAndScreenId(UUID functionId, UUID screenId);
    Optional<FunctionScreen> findByFunctionIdAndScreenId(UUID functionId, UUID screenId);
    List<FunctionScreen> findByFunctionId(UUID functionId);
    List<FunctionScreen> findByFunctionIdIn(Collection<UUID> functionIds);
    List<FunctionScreen> findByScreenId(UUID screenId);
    void deleteByFunctionIdAndScreenId(UUID functionId, UUID screenId);
}
