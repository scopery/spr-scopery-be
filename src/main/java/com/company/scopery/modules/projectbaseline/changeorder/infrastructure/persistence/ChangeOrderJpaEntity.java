package com.company.scopery.modules.projectbaseline.changeorder.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = ProjectBaselineTableNames.CHANGE_ORDER)
@Getter @Setter @NoArgsConstructor
public class ChangeOrderJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="change_request_id", nullable=false) private UUID changeRequestId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="commercial_impact_json", columnDefinition="jsonb") private String commercialImpactJson;
    @Column(name="source_quote_version_id") private UUID sourceQuoteVersionId;
    @Column(name="future_contract_id") private UUID futureContractId;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="rejected_at") private Instant rejectedAt;
    @Column(name="rejected_by") private UUID rejectedBy;
    @Column(name="rejection_reason", columnDefinition="text") private String rejectionReason;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
