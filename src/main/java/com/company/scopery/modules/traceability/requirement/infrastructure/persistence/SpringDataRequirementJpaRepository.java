package com.company.scopery.modules.traceability.requirement.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataRequirementJpaRepository extends JpaRepository<RequirementJpaEntity, UUID> {
    Optional<RequirementJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<RequirementJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
