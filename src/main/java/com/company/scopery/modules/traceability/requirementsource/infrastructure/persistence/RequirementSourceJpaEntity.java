package com.company.scopery.modules.traceability.requirementsource.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.REQUIREMENT_SOURCE) @Getter @Setter @NoArgsConstructor
public class RequirementSourceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "requirement_id", nullable = false) private UUID requirementId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "source_type", nullable = false) private String sourceType;
    @Column(name = "source_reference", nullable = false) private String sourceReference;
    @Column(columnDefinition = "text") private String description;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
