package com.company.scopery.modules.project.timeline.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskDailyAllocationJpaRepository
        extends JpaRepository<TaskDailyAllocationJpaEntity, UUID> {

    List<TaskDailyAllocationJpaEntity> findAllByProjectId(UUID projectId);

    List<TaskDailyAllocationJpaEntity> findAllByTaskId(UUID taskId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from TaskDailyAllocationJpaEntity a where a.taskId = :taskId and a.source = :source")
    void deleteByTaskIdAndSource(@Param("taskId") UUID taskId, @Param("source") String source);
}
