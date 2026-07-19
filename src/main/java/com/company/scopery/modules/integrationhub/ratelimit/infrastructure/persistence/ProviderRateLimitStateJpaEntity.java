package com.company.scopery.modules.integrationhub.ratelimit.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.RATE_LIMIT) @Getter @Setter @NoArgsConstructor
public class ProviderRateLimitStateJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id", nullable=false) private UUID connectionId;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(nullable=false) private String status;
    @Column(name="limit_name") private String limitName;
    @Column(name="remaining_count") private Long remainingCount;
    @Column(name="reset_at") private Instant resetAt;
    @Column(name="backoff_until") private Instant backoffUntil;
    @Column(name="last_updated_at", nullable=false) private Instant lastUpdatedAt;
    @Version private Integer version;
}
