package com.company.scopery.modules.servicesupport.statushistory.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.STATUS_HISTORY) @Getter @Setter @NoArgsConstructor
public class SupportStatusHistoryJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "support_case_id", nullable = false) private UUID supportCaseId;
    @Column(name = "from_status") private String fromStatus;
    @Column(name = "to_status", nullable = false) private String toStatus;
    @Column private String reason;
    @Column(name = "changed_at", nullable = false) private Instant changedAt;
    @Column(name = "changed_by") private UUID changedBy;
    @Column(nullable = false) private String visibility;
    @Version private Integer version;
}
