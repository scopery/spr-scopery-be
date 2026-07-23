package com.company.scopery.modules.traceability.businessrule.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessRuleRepository {

    BusinessRule save(BusinessRule rule);

    Optional<BusinessRule> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);

    List<BusinessRule> findByFunctionalItemId(UUID functionalItemId);

    boolean existsByFunctionalItemIdAndCode(UUID functionalItemId, String code);

    void delete(UUID id, UUID functionalItemId);
}
