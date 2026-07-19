package com.company.scopery.modules.traceability.screen.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.SCREEN) @Getter @Setter @NoArgsConstructor
public class RegistryScreenJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="application_id", nullable=false) private UUID applicationId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(name="route_path") private String routePath;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
