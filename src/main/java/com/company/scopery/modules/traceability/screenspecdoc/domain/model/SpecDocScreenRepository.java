package com.company.scopery.modules.traceability.screenspecdoc.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecDocScreenRepository {
    List<SpecDocScreen> findByDocumentId(UUID documentId);
    Optional<SpecDocScreen> findByDocumentIdAndScreenId(UUID documentId, UUID screenId);
    boolean existsByDocumentIdAndScreenId(UUID documentId, UUID screenId);
    SpecDocScreen save(SpecDocScreen link);
    void deleteByDocumentIdAndScreenId(UUID documentId, UUID screenId);
}
