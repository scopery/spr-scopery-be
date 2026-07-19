package com.company.scopery.modules.governance.objecttype.domain.model;
import java.util.List; import java.util.Optional;
public interface GovernedObjectTypeRepository {
    GovernedObjectType save(GovernedObjectType objectType);
    Optional<GovernedObjectType> findByCode(String objectTypeCode);
    List<GovernedObjectType> findAllEnabled();
    boolean existsByCode(String objectTypeCode);
}
