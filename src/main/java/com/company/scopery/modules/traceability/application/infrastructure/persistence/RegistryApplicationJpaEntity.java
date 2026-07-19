package com.company.scopery.modules.traceability.application.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.APPLICATION) @Getter @Setter @NoArgsConstructor
public class RegistryApplicationJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Version private Integer version;
}
