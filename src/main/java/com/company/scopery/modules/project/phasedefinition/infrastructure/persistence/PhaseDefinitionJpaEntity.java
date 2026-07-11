package com.company.scopery.modules.project.phasedefinition.infrastructure.persistence;

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
        name = ProjectTableNames.PHASE_DEFINITION,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_project_phase_definition_system_code",
                        columnNames = {"scope", "code"}
                )
        },
        indexes = {
                @Index(name = "idx_project_phase_definition_scope", columnList = "scope"),
                @Index(name = "idx_project_phase_definition_status", columnList = "status"),
                @Index(name = "idx_project_phase_definition_workspace_id", columnList = "workspace_id")
        }
)
public class PhaseDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "scope", nullable = false, length = 50)
    private String scope;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_system_default", nullable = false)
    private boolean isSystemDefault;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
