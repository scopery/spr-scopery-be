package com.company.scopery.modules.productivity.favorite.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.FAVORITE) @Getter @Setter @NoArgsConstructor
public class FavoriteItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="label_override") private String labelOverride;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
