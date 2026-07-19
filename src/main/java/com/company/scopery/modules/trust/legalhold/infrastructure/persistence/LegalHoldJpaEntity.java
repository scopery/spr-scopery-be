package com.company.scopery.modules.trust.legalhold.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.LEGAL_HOLD) @Getter @Setter @NoArgsConstructor
public class LegalHoldJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="hold_code") private String holdCode;
    @Column(name="hold_type", nullable=false) private String holdType;
    @Column(name="scope_type", nullable=false) private String scopeType;
    @Column(name="scope_id") private UUID scopeId;
    @Column(nullable=false) private String reason;
    @Column(nullable=false) private String status;
    @Column(name="released_at") private Instant releasedAt;
    @Column(name="release_reason") private String releaseReason;
    @Version private Integer version;
}
