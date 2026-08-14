package com.company.scopery.modules.traceability.dataentityfield.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.DATA_ENTITY_FIELD)
@Getter
@Setter
@NoArgsConstructor
public class RegistryDataEntityFieldJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "is_nullable", nullable = false)
    private boolean isNullable;

    @Column(name = "is_unique", nullable = false)
    private boolean isUnique;

    @Column(columnDefinition = "text")
    private String remark;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
