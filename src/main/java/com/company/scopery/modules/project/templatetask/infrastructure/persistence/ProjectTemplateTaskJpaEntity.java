package com.company.scopery.modules.project.templatetask.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = ProjectTableNames.PROJECT_TEMPLATE_TASK)
public class ProjectTemplateTaskJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_version_id", nullable = false, updatable = false)
    private UUID templateVersionId;

    @Column(name = "template_phase_id", nullable = false)
    private UUID templatePhaseId;

    @Column(name = "template_wbs_node_id")
    private UUID templateWbsNodeId;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_priority", length = 50)
    private String defaultPriority;

    @Column(name = "estimate_hours", precision = 12, scale = 2)
    private BigDecimal estimateHours;

    @Column(name = "due_offset_days")
    private Integer dueOffsetDays;

    @Column(name = "start_offset_days")
    private Integer startOffsetDays;

    @Column(name = "default_assignee_role_code", length = 100)
    private String defaultAssigneeRoleCode;

    @Column(name = "deliverable_document_type_id")
    private UUID deliverableDocumentTypeId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
