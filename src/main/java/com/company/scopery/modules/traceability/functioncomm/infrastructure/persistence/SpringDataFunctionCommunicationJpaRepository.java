package com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionCommunicationJpaRepository
        extends JpaRepository<FunctionCommunicationJpaEntity, FunctionCommunicationId> {

    boolean existsByIdFunctionIdAndIdCommunicationId(UUID functionId, UUID communicationId);

    Optional<FunctionCommunicationJpaEntity> findByIdFunctionIdAndIdCommunicationId(UUID functionId, UUID communicationId);

    List<FunctionCommunicationJpaEntity> findByIdFunctionId(UUID functionId);

    List<FunctionCommunicationJpaEntity> findByIdFunctionIdIn(Collection<UUID> functionIds);

    List<FunctionCommunicationJpaEntity> findByIdCommunicationId(UUID communicationId);

    void deleteByIdFunctionIdAndIdCommunicationId(UUID functionId, UUID communicationId);
}
