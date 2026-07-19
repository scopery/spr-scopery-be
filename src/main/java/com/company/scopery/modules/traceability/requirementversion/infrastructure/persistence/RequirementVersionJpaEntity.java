package com.company.scopery.modules.traceability.requirementversion.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.REQUIREMENT_VERSION) @Getter @Setter @NoArgsConstructor
public class RequirementVersionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "requirement_id", nullable = false) private UUID requirementId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "version_number", nullable = false) private Integer versionNumber;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "change_summary", columnDefinition = "text") private String changeSummary;
    @Column(name = "created_by_user_id") private UUID createdByUserId;
    @Version private Integer version;
}
