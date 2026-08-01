package com.company.scopery.modules.traceability.commspec.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunicationSpecificationRepository {
    CommunicationSpecification save(CommunicationSpecification spec);
    Optional<CommunicationSpecification> findById(UUID id);
    Optional<CommunicationSpecification> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<CommunicationSpecification> findByApplicationId(UUID applicationId);
    List<CommunicationSpecification> findByIdIn(Collection<UUID> ids);
    boolean existsByApplicationIdAndCode(UUID applicationId, String code);
}
