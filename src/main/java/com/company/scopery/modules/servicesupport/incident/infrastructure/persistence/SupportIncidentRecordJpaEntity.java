package com.company.scopery.modules.servicesupport.incident.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.INCIDENT_RECORD) @Getter @Setter @NoArgsConstructor
public class SupportIncidentRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "incident_number") private String incidentNumber;
    @Column(nullable = false) private String title;
    @Column private String description;
    @Column(nullable = false) private String severity;
    @Column(nullable = false) private String status;
    @Column(name = "impact_summary") private String impactSummary;
    @Column(name = "client_visible_summary") private String clientVisibleSummary;
    @Column(name = "owner_user_id") private UUID ownerUserId;
    @Column(name = "detected_at") private Instant detectedAt;
    @Column(name = "acknowledged_at") private Instant acknowledgedAt;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "closed_at") private Instant closedAt;
    @Version private Integer version;
}
