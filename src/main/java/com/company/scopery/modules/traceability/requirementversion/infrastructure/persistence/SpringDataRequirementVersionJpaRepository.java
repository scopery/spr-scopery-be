package com.company.scopery.modules.traceability.requirementversion.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRequirementVersionJpaRepository extends JpaRepository<RequirementVersionJpaEntity, UUID> {
    List<RequirementVersionJpaEntity> findByRequirementIdOrderByVersionNumberDesc(UUID requirementId);
    Optional<RequirementVersionJpaEntity> findByIdAndRequirementId(UUID id, UUID requirementId);
    int countByRequirementId(UUID requirementId);
}
