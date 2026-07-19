package com.company.scopery.modules.project.templatephase.infrastructure.persistence;

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
@Table(name = ProjectTableNames.PROJECT_TEMPLATE_PHASE)
public class ProjectTemplatePhaseJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_version_id", nullable = false, updatable = false)
    private UUID templateVersionId;

    @Column(name = "phase_definition_id")
    private UUID phaseDefinitionId;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "default_duration_days")
    private Integer defaultDurationDays;

    @Column(name = "start_offset_days")
    private Integer startOffsetDays;

    @Column(name = "deliverable_document_type_id")
    private UUID deliverableDocumentTypeId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
