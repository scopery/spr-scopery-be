package com.company.scopery.modules.project.taskdependency.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = ProjectTableNames.TASK_DEPENDENCY,
        indexes = {
                @Index(name = "idx_project_task_dependency_project_id", columnList = "project_id"),
                @Index(name = "idx_project_task_dependency_predecessor_task_id", columnList = "predecessor_task_id"),
                @Index(name = "idx_project_task_dependency_successor_task_id", columnList = "successor_task_id"),
                @Index(name = "idx_project_task_dependency_status", columnList = "status")
        }
)
public class TaskDependencyJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "predecessor_task_id", nullable = false, updatable = false)
    private UUID predecessorTaskId;

    @Column(name = "successor_task_id", nullable = false, updatable = false)
    private UUID successorTaskId;

    @Column(name = "dependency_type", nullable = false, length = 50)
    private String dependencyType;

    @Column(name = "lag_days", nullable = false)
    private Integer lagDays;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
