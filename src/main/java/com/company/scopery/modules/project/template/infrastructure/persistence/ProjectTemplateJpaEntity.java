package com.company.scopery.modules.project.template.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = ProjectTableNames.PROJECT_TEMPLATE)
public class ProjectTemplateJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "scope", nullable = false, length = 50)
    private String scope;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "visibility", length = 50)
    private String visibility;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_version_id")
    private UUID currentVersionId;

    @Column(name = "built_in", nullable = false)
    private boolean builtIn;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
