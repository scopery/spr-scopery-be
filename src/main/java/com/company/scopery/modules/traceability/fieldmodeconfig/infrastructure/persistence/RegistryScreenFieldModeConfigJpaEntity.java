package com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.SCREEN_FIELD_MODE_CONFIG)
@Getter
@Setter
@NoArgsConstructor
public class RegistryScreenFieldModeConfigJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "field_id", nullable = false)
    private UUID fieldId;

    @Column(name = "mode_id", nullable = false)
    private UUID modeId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "is_readonly", nullable = false)
    private boolean readonly;

    @Column(name = "default_value", columnDefinition = "text")
    private String defaultValue;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Version
    private Integer version;
}
