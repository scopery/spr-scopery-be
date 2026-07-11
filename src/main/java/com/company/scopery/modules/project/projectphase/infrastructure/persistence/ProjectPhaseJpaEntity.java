package com.company.scopery.modules.project.projectphase.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = ProjectTableNames.PROJECT_PHASE,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_project_project_phase_project_code",
                        columnNames = {"project_id", "code"}
                ),
                @UniqueConstraint(
                        name = "uq_project_project_phase_project_display_order",
                        columnNames = {"project_id", "display_order"}
                )
        },
        indexes = {
                @Index(name = "idx_project_project_phase_project_id", columnList = "project_id"),
                @Index(name = "idx_project_project_phase_status", columnList = "status"),
                @Index(name = "idx_project_project_phase_phase_definition_id", columnList = "phase_definition_id")
        }
)
public class ProjectPhaseJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "phase_definition_id")
    private UUID phaseDefinitionId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
