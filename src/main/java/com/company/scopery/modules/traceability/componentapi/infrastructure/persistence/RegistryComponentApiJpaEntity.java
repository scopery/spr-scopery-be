package com.company.scopery.modules.traceability.componentapi.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.COMPONENT_API)
@Getter @Setter @NoArgsConstructor
public class RegistryComponentApiJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "component_id", nullable = false) private UUID componentId;
    @Column(name = "api_id", nullable = false) private UUID apiId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(nullable = false) private String role;
    @Column(columnDefinition = "text") private String note;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
