package com.company.scopery.modules.traceability.screensection.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.SCREEN_SECTION) @Getter @Setter @NoArgsConstructor
public class RegistryScreenSectionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="screen_id", nullable=false) private UUID screenId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(name="display_order", nullable=false) private int displayOrder;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
