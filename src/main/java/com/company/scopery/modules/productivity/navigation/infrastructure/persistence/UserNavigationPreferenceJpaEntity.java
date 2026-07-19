package com.company.scopery.modules.productivity.navigation.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.NAV_PREF) @Getter @Setter @NoArgsConstructor
public class UserNavigationPreferenceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="preference_json") private String preferenceJson;
    @Column(name="default_landing_route") private String defaultLandingRoute;
    @Version private Integer version;
}
