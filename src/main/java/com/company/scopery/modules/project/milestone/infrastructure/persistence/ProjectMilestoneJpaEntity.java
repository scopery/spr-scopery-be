package com.company.scopery.modules.project.milestone.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = ProjectTableNames.PROJECT_MILESTONE,
        indexes = {
                @Index(name = "idx_project_milestone_project_id", columnList = "project_id"),
                @Index(name = "idx_project_milestone_milestone_date", columnList = "milestone_date"),
                @Index(name = "idx_project_milestone_status", columnList = "status")
        }
)
public class ProjectMilestoneJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "project_phase_id")
    private UUID projectPhaseId;

    @Column(name = "wbs_node_id")
    private UUID wbsNodeId;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "milestone_date", nullable = false)
    private LocalDate milestoneDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "achieved_at")
    private Instant achievedAt;

    @Column(name = "achieved_by")
    private UUID achievedBy;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
