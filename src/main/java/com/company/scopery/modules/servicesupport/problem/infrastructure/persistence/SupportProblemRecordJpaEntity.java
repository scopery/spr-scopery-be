package com.company.scopery.modules.servicesupport.problem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.PROBLEM_RECORD) @Getter @Setter @NoArgsConstructor
public class SupportProblemRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "problem_number") private String problemNumber;
    @Column(nullable = false) private String title;
    @Column private String description;
    @Column(nullable = false) private String status;
    @Column(name = "root_cause_summary") private String rootCauseSummary;
    @Column private String workaround;
    @Column(name = "owner_user_id") private UUID ownerUserId;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "resolved_by") private UUID resolvedBy;
    @Column(name = "closed_at") private Instant closedAt;
    @Column(name = "closed_by") private UUID closedBy;
    @Version private Integer version;
}
