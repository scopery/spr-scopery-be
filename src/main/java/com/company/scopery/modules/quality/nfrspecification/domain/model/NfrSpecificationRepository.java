package com.company.scopery.modules.quality.nfrspecification.domain.model;
import java.util.Optional; import java.util.UUID;
public interface NfrSpecificationRepository {
    Optional<NfrSpecification> findByRequirementId(UUID requirementId);
    NfrSpecification save(NfrSpecification e);
}
