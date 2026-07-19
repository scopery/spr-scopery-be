package com.company.scopery.modules.project.milestone.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectMilestoneJpaRepository extends JpaRepository<ProjectMilestoneJpaEntity, UUID> {
    List<ProjectMilestoneJpaEntity> findAllByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
