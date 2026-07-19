package com.company.scopery.modules.collaboration.mention.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.MENTION) @Getter @Setter @NoArgsConstructor
public class MentionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="source_type", nullable=false) private String sourceType;
    @Column(name="source_id", nullable=false) private UUID sourceId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id") private UUID targetId;
    @Column(name="notification_sent", nullable=false) private boolean notificationSent;
    @Version private Integer version;
}
