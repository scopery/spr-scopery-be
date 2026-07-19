package com.company.scopery.modules.externalparty.stakeholder.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataProjectStakeholderJpaRepository extends JpaRepository<ProjectStakeholderJpaEntity, UUID> {
    Optional<ProjectStakeholderJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectStakeholderJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
