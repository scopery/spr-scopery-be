package com.company.scopery.modules.traceability.componentfield.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.COMPONENT_FIELD)
@Getter
@Setter
@NoArgsConstructor
public class RegistryComponentFieldJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "component_id", nullable = false)
    private UUID componentId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "field_key", nullable = false)
    private String fieldKey;

    @Column(nullable = false)
    private String label;

    @Column(name = "field_type", nullable = false)
    private String fieldType;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(columnDefinition = "text")
    private String remark;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
