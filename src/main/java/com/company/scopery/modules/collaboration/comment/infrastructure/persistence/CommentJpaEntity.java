package com.company.scopery.modules.collaboration.comment.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.COMMENT) @Getter @Setter @NoArgsConstructor
public class CommentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="thread_id", nullable=false) private UUID threadId;
    @Column(name="parent_comment_id") private UUID parentCommentId;
    @Column(name="author_type", nullable=false) private String authorType;
    @Column(name="author_id") private UUID authorId;
    @Column(name="author_display_name_snapshot") private String authorDisplayNameSnapshot;
    @Column(nullable=false, columnDefinition="text") private String body;
    @Column(nullable=false) private String status;
    @Column(name="internal_only", nullable=false) private boolean internalOnly;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="edited_at") private Instant editedAt; @Column(name="edited_by") private UUID editedBy;
    @Column(name="deleted_at") private Instant deletedAt; @Column(name="deleted_by") private UUID deletedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
