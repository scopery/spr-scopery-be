package com.company.scopery.modules.traceability.requirement.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=TraceabilityTableNames.REQUIREMENT) @Getter @Setter @NoArgsConstructor
public class RequirementJpaEntity extends AuditableJpaEntity {
    @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId; @Column(name="application_id") private UUID applicationId;
    private String code; @Column(nullable=false) private String title; @Column(columnDefinition="text") private String description;
    @Column(name="requirement_type", nullable=false) private String requirementType;
    @Column(nullable=false) private String priority; @Column(nullable=false) private String status;
    @Column(name="owner_user_id") private UUID ownerUserId; @Column(name="current_version_number", nullable=false) private int currentVersionNumber;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Column(name="functional_item_id") private UUID functionalItemId;
    @Column(name="non_functional_item_id") private UUID nonFunctionalItemId;
    @Version private Integer version;
}
