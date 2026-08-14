package com.company.scopery.modules.traceability.componentoption.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.COMPONENT_OPTION)
@Getter
@Setter
@NoArgsConstructor
public class RegistryComponentOptionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "component_id", nullable = false)
    private UUID componentId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @Column(name = "option_label", nullable = false)
    private String optionLabel;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
