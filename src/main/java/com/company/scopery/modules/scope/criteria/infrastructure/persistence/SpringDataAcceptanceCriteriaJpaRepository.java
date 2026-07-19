package com.company.scopery.modules.scope.criteria.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataAcceptanceCriteriaJpaRepository extends JpaRepository<AcceptanceCriteriaJpaEntity, UUID> {
    List<AcceptanceCriteriaJpaEntity> findByDeliverableIdOrderByCreatedAtAsc(UUID deliverableId);
}
