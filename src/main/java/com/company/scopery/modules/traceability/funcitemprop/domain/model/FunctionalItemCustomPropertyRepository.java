package com.company.scopery.modules.traceability.funcitemprop.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionalItemCustomPropertyRepository {
    FunctionalItemCustomProperty save(FunctionalItemCustomProperty prop);
    Optional<FunctionalItemCustomProperty> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);
    List<FunctionalItemCustomProperty> findByFunctionalItemId(UUID functionalItemId);
    boolean existsByFunctionalItemIdAndPropKey(UUID functionalItemId, String propKey);
    void delete(UUID id, UUID functionalItemId);
}
