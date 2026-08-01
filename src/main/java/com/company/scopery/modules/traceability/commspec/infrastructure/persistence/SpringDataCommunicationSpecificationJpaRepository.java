package com.company.scopery.modules.traceability.commspec.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCommunicationSpecificationJpaRepository
        extends JpaRepository<CommunicationSpecificationJpaEntity, UUID> {
    Optional<CommunicationSpecificationJpaEntity> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<CommunicationSpecificationJpaEntity> findByApplicationIdOrderByCodeAsc(UUID applicationId);
    List<CommunicationSpecificationJpaEntity> findByIdIn(Collection<UUID> ids);
    boolean existsByApplicationIdAndCode(UUID applicationId, String code);
}
