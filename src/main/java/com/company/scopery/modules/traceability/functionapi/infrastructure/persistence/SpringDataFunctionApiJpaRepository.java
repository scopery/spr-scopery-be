package com.company.scopery.modules.traceability.functionapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionApiJpaRepository extends JpaRepository<FunctionApiJpaEntity, FunctionApiId> {

    boolean existsByIdFunctionIdAndIdApiEndpointId(UUID functionId, UUID apiEndpointId);

    Optional<FunctionApiJpaEntity> findByIdFunctionIdAndIdApiEndpointId(UUID functionId, UUID apiEndpointId);

    List<FunctionApiJpaEntity> findByIdFunctionId(UUID functionId);

    List<FunctionApiJpaEntity> findByIdFunctionIdIn(Collection<UUID> functionIds);

    List<FunctionApiJpaEntity> findByIdApiEndpointId(UUID apiEndpointId);

    void deleteByIdFunctionIdAndIdApiEndpointId(UUID functionId, UUID apiEndpointId);
}
