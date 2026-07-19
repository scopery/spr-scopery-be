package com.company.scopery.modules.traceability.requirementcriteria.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.REQUIREMENT_CRITERIA) @Getter @Setter @NoArgsConstructor
public class RequirementCriteriaJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "requirement_id", nullable = false) private UUID requirementId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(name = "acceptance_type", nullable = false, length = 100) private String acceptanceType;
    @Column(nullable = false, length = 50) private String status;
    @Column(name = "display_order", nullable = false) private Integer displayOrder;
    @Version private Integer version;
}
