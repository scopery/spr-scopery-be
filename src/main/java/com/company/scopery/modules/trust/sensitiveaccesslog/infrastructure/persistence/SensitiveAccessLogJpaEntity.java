package com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.SENSITIVE_ACCESS_LOG) @Getter @Setter @NoArgsConstructor
public class SensitiveAccessLogJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "actor_principal_type", length = 50) private String actorPrincipalType;
    @Column(name = "actor_user_id") private UUID actorUserId;
    @Column(name = "target_object_type", length = 100, nullable = false) private String targetObjectType;
    @Column(name = "target_object_id") private UUID targetObjectId;
    @Column(name = "field_path", length = 500) private String fieldPath;
    @Column(length = 50, nullable = false) private String classification;
    @Column(name = "access_action", length = 50, nullable = false) private String accessAction;
    @Column(name = "access_channel", length = 50) private String accessChannel;
    @Column(columnDefinition = "TEXT") private String reason;
    @Column(name = "request_path", length = 500) private String requestPath;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @Column(name = "trace_id", length = 100) private String traceId;
    @Version private Integer version;
}
