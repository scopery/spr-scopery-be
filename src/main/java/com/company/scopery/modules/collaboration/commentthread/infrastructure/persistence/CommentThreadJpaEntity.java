package com.company.scopery.modules.collaboration.commentthread.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.COMMENT_THREAD) @Getter @Setter @NoArgsConstructor
public class CommentThreadJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    private String title;
    @Column(nullable=false) private String status;
    @Column(name="internal_only", nullable=false) private boolean internalOnly;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="resolved_at") private Instant resolvedAt; @Column(name="resolved_by") private UUID resolvedBy;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
