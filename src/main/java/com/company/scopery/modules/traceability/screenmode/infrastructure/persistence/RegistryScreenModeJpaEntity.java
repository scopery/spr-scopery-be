package com.company.scopery.modules.traceability.screenmode.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.SCREEN_MODE)
@Getter
@Setter
@NoArgsConstructor
public class RegistryScreenModeJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "screen_id", nullable = false)
    private UUID screenId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "mode_code", nullable = false)
    private String modeCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
