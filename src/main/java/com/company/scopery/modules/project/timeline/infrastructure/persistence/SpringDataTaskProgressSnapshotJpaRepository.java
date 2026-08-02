package com.company.scopery.modules.project.timeline.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTaskProgressSnapshotJpaRepository
        extends JpaRepository<TaskProgressSnapshotJpaEntity, UUID> {

    List<TaskProgressSnapshotJpaEntity> findAllByProjectId(UUID projectId);

    List<TaskProgressSnapshotJpaEntity> findAllByTaskId(UUID taskId);

    Optional<TaskProgressSnapshotJpaEntity> findByTaskIdAndSnapshotDate(UUID taskId, LocalDate snapshotDate);
}
