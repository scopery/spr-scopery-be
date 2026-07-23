package com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.FUNCTIONAL_ITEM)
@Getter
@Setter
@NoArgsConstructor
public class FunctionalItemJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "module_id")
    private UUID moduleId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "acceptance_criteria", columnDefinition = "jsonb")
    private String acceptanceCriteria;

    @Version
    private Integer version;
}
