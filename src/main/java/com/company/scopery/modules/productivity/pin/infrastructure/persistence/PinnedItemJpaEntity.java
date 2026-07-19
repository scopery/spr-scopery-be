package com.company.scopery.modules.productivity.pin.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.PIN) @Getter @Setter @NoArgsConstructor
public class PinnedItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String scope;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
