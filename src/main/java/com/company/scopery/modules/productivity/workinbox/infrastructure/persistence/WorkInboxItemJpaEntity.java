package com.company.scopery.modules.productivity.workinbox.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.WORK_INBOX) @Getter @Setter @NoArgsConstructor
public class WorkInboxItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="source_type", nullable=false) private String sourceType;
    @Column(name="source_id", nullable=false) private UUID sourceId;
    @Column(name="action_type", nullable=false) private String actionType;
    @Column(nullable=false) private String title;
    private String priority;
    @Column(name="due_at") private Instant dueAt;
    @Column(nullable=false) private String status;
    @Column(name="read_at") private Instant readAt;
    @Column(name="dismissed_at") private Instant dismissedAt;
    @Column(name="snoozed_until") private Instant snoozedUntil;
    @Version private Integer version;
}
