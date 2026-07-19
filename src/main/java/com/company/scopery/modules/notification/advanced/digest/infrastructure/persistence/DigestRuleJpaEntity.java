package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.DIGEST_RULE) @Getter @Setter @NoArgsConstructor
public class DigestRuleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    private String scope;
    @Column(nullable=false) private String frequency;
    @Column(name="schedule_config_json") private String scheduleConfigJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
