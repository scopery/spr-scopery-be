package com.company.scopery.modules.traceability.requirementcriteria.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRequirementCriteriaJpaRepository extends JpaRepository<RequirementCriteriaJpaEntity, UUID> {
    List<RequirementCriteriaJpaEntity> findByRequirementIdOrderByDisplayOrderAsc(UUID requirementId);
    Optional<RequirementCriteriaJpaEntity> findByIdAndRequirementId(UUID id, UUID requirementId);
}
