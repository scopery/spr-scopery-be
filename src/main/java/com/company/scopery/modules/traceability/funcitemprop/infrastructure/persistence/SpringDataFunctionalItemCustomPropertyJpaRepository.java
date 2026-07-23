package com.company.scopery.modules.traceability.funcitemprop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionalItemCustomPropertyJpaRepository
        extends JpaRepository<FunctionalItemCustomPropertyJpaEntity, UUID> {

    List<FunctionalItemCustomPropertyJpaEntity> findByFunctionalItemIdOrderByCreatedAtAsc(UUID functionalItemId);

    Optional<FunctionalItemCustomPropertyJpaEntity> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);

    boolean existsByFunctionalItemIdAndPropKey(UUID functionalItemId, String propKey);

    void deleteByIdAndFunctionalItemId(UUID id, UUID functionalItemId);
}
