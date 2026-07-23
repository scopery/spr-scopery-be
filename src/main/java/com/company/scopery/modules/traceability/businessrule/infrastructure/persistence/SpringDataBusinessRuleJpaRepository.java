package com.company.scopery.modules.traceability.businessrule.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataBusinessRuleJpaRepository extends JpaRepository<BusinessRuleJpaEntity, UUID> {

    List<BusinessRuleJpaEntity> findByFunctionalItemIdOrderByCreatedAtDesc(UUID functionalItemId);

    Optional<BusinessRuleJpaEntity> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);

    boolean existsByFunctionalItemIdAndCode(UUID functionalItemId, String code);

    void deleteByIdAndFunctionalItemId(UUID id, UUID functionalItemId);
}
