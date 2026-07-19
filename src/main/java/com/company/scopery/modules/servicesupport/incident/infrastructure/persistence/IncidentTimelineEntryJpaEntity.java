package com.company.scopery.modules.servicesupport.incident.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.INCIDENT_TIMELINE) @Getter @Setter @NoArgsConstructor
public class IncidentTimelineEntryJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "incident_id", nullable = false) private UUID incidentId;
    @Column(name = "entry_type", nullable = false) private String entryType;
    @Column(nullable = false) private String visibility;
    @Column(nullable = false) private String message;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @Version private Integer version;
}
