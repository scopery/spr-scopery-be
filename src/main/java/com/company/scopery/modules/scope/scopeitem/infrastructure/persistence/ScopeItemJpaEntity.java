package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ScopeTableNames.ITEM) @Getter @Setter @NoArgsConstructor
public class ScopeItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="scope_package_id", nullable=false) private UUID scopePackageId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="source_quote_line_id") private UUID sourceQuoteLineId;
    @Column(name="source_change_request_id") private UUID sourceChangeRequestId;
    @Column(name="parent_scope_item_id") private UUID parentScopeItemId;
    @Column(nullable=false) private String type;
    private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="in_scope", nullable=false) private boolean inScope;
    @Column(name="out_of_scope", nullable=false) private boolean outOfScope;
    private String priority;
    @Column(name="acceptance_required", nullable=false) private boolean acceptanceRequired;
    @Column(nullable=false) private String status;
    @Column(name="sort_order") private Integer sortOrder;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
