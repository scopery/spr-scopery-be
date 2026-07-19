package com.company.scopery.modules.project.templatedependency.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = ProjectTableNames.PROJECT_TEMPLATE_TASK_DEPENDENCY)
public class ProjectTemplateTaskDependencyJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_version_id", nullable = false, updatable = false)
    private UUID templateVersionId;

    @Column(name = "predecessor_template_task_id", nullable = false)
    private UUID predecessorTemplateTaskId;

    @Column(name = "successor_template_task_id", nullable = false)
    private UUID successorTemplateTaskId;

    @Column(name = "dependency_type", nullable = false, length = 50)
    private String dependencyType;

    @Column(name = "lag_days", nullable = false)
    private int lagDays;
}
