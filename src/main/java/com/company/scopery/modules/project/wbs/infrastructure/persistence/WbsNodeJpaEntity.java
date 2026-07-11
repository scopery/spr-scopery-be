package com.company.scopery.modules.project.wbs.infrastructure.persistence;

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
@Table(
        name = ProjectTableNames.WBS_NODE,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_project_wbs_node_project_code", columnNames = {"project_id", "code"})
        },
        indexes = {
                @Index(name = "idx_project_wbs_node_project_id", columnList = "project_id"),
                @Index(name = "idx_project_wbs_node_phase_id", columnList = "project_phase_id"),
                @Index(name = "idx_project_wbs_node_parent_id", columnList = "parent_id"),
                @Index(name = "idx_project_wbs_node_status", columnList = "status"),
                @Index(name = "idx_project_wbs_node_path", columnList = "path")
        }
)
public class WbsNodeJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "project_phase_id", nullable = false)
    private UUID projectPhaseId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "node_type", nullable = false, length = 50)
    private String nodeType;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
