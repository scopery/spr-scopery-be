package com.company.scopery.modules.scope.scopepackage.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ScopeTableNames.PACKAGE) @Getter @Setter @NoArgsConstructor
public class ScopePackageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="source_quote_version_id") private UUID sourceQuoteVersionId;
    @Column(name="source_baseline_id") private UUID sourceBaselineId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
