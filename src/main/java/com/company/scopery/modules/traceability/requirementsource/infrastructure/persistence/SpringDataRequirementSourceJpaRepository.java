package com.company.scopery.modules.traceability.requirementsource.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRequirementSourceJpaRepository extends JpaRepository<RequirementSourceJpaEntity, UUID> {
    List<RequirementSourceJpaEntity> findByRequirementIdOrderByCreatedAtDesc(UUID requirementId);
    Optional<RequirementSourceJpaEntity> findByIdAndRequirementId(UUID id, UUID requirementId);
}
