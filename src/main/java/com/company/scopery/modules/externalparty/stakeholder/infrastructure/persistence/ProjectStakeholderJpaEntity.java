package com.company.scopery.modules.externalparty.stakeholder.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ExternalPartyTableNames.STAKEHOLDER) @Getter @Setter @NoArgsConstructor
public class ProjectStakeholderJpaEntity extends AuditableJpaEntity {
    @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId; @Column(name="contact_id") private UUID contactId;
    @Column(name="organization_id") private UUID organizationId; @Column(name="internal_user_id") private UUID internalUserId;
    @Column(name="stakeholder_role", nullable=false) private String stakeholderRole;
    private String influence; private String interest; @Column(nullable=false) private String status;
    @Column(name="client_facing", nullable=false) private boolean clientFacing;
    @Column(name="archived_at") private Instant archivedAt; @Version private Integer version;
}
