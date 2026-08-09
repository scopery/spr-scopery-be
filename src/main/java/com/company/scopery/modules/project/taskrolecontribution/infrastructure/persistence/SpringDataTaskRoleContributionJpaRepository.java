package com.company.scopery.modules.project.taskrolecontribution.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskRoleContributionJpaRepository
        extends JpaRepository<TaskRoleContributionJpaEntity, UUID> {

    List<TaskRoleContributionJpaEntity> findAllByTaskId(UUID taskId);
    List<TaskRoleContributionJpaEntity> findAllByProjectId(UUID projectId);
}
