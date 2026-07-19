package com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_PROFILE) @Getter @Setter @NoArgsConstructor
public class ResourceProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="linked_user_id") private UUID linkedUserId;
    @Column(name="linked_workspace_member_id") private UUID linkedWorkspaceMemberId;
    @Column(name="linked_team_id") private UUID linkedTeamId;
    @Column(name="linked_external_contact_id") private UUID linkedExternalContactId;
    @Column(name="resource_type", nullable=false) private String resourceType;
    @Column(name="display_name", nullable=false) private String displayName;
    @Column(name="primary_role_id") private UUID primaryRoleId;
    @Column(name="default_calendar_id") private UUID defaultCalendarId;
    @Column(name="default_rate_card_id") private UUID defaultRateCardId;
    private String timezone;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
