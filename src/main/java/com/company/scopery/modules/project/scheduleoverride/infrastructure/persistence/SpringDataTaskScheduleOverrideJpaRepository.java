package com.company.scopery.modules.project.scheduleoverride.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTaskScheduleOverrideJpaRepository extends JpaRepository<TaskScheduleOverrideJpaEntity, UUID> {
    Optional<TaskScheduleOverrideJpaEntity> findByTaskIdAndStatus(UUID taskId, String status);
    List<TaskScheduleOverrideJpaEntity> findAllByProjectIdAndStatus(UUID projectId, String status);
}
