package com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.NON_FUNCTIONAL_ITEM)
@Getter
@Setter
@NoArgsConstructor
public class NonFunctionalItemJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "target_metric", columnDefinition = "text")
    private String targetMetric;

    @Column(name = "scope_type", nullable = false)
    private String scopeType;

    @Column(name = "scope_ref_id")
    private UUID scopeRefId;

    @Version
    @Column(name = "version")
    private Integer version;
}
