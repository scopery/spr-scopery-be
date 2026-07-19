package com.company.scopery.modules.resourcecapacity.conflict.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataAssignmentConflictJpaRepository extends JpaRepository<AssignmentConflictJpaEntity, UUID> {
    List<AssignmentConflictJpaEntity> findByProjectId(UUID projectId);
}
