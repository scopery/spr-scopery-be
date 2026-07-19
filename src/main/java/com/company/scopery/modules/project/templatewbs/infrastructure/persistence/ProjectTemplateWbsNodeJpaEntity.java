package com.company.scopery.modules.project.templatewbs.infrastructure.persistence;

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
@Table(name = ProjectTableNames.PROJECT_TEMPLATE_WBS_NODE)
public class ProjectTemplateWbsNodeJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_version_id", nullable = false, updatable = false)
    private UUID templateVersionId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "template_phase_id")
    private UUID templatePhaseId;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "node_type", length = 50)
    private String nodeType;

    @Column(name = "depth", nullable = false)
    private int depth;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "deliverable_document_type_id")
    private UUID deliverableDocumentTypeId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
