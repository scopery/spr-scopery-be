package com.company.scopery.modules.traceability.apiendpoint.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.API_ENDPOINT) @Getter @Setter @NoArgsConstructor
public class RegistryApiEndpointJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="application_id", nullable=false) private UUID applicationId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String method;
    @Column(name="path_pattern", nullable=false) private String pathPattern;
    @Column private String name;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
