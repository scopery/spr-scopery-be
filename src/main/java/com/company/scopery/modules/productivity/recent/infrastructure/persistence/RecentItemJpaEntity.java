package com.company.scopery.modules.productivity.recent.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.RECENT) @Getter @Setter @NoArgsConstructor
public class RecentItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="principal_type", nullable=false) private String principalType;
    @Column(name="user_id") private UUID userId;
    @Column(name="external_portal_account_id") private UUID externalPortalAccountId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="title_snapshot") private String titleSnapshot;
    @Column(name="viewed_at", nullable=false) private Instant viewedAt;
    @Version private Integer version;
}
