package com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionScreenJpaRepository extends JpaRepository<FunctionScreenJpaEntity, FunctionScreenId> {
    boolean existsByIdFunctionIdAndIdScreenId(UUID functionId, UUID screenId);
    Optional<FunctionScreenJpaEntity> findByIdFunctionIdAndIdScreenId(UUID functionId, UUID screenId);
    List<FunctionScreenJpaEntity> findByIdFunctionId(UUID functionId);
    List<FunctionScreenJpaEntity> findByIdFunctionIdIn(Collection<UUID> functionIds);
    List<FunctionScreenJpaEntity> findByIdScreenId(UUID screenId);
    void deleteByIdFunctionIdAndIdScreenId(UUID functionId, UUID screenId);
}
