package com.company.scopery.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "app_audit_event")
public class ImmutableAuditEventJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "event_type", nullable = false, updatable = false) private String eventType;
    @Column(name = "actor_id", updatable = false) private UUID actorId;
    @Column(name = "actor_type", nullable = false, updatable = false) private String actorType;
    @Column(name = "resource_type", updatable = false) private String resourceType;
    @Column(name = "resource_ref_id", updatable = false) private UUID resourceRefId;
    @Column(name = "organization_id", updatable = false) private UUID organizationId;
    @Column(name = "workspace_id", updatable = false) private UUID workspaceId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "before_state", columnDefinition = "jsonb", updatable = false) private String beforeState;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "after_state", columnDefinition = "jsonb", updatable = false) private String afterState;
    @Column(name = "reason", columnDefinition = "TEXT", updatable = false) private String reason;
    @Column(name = "trace_id", updatable = false) private String traceId;
    @Column(name = "occurred_at", nullable = false, updatable = false) private Instant occurredAt;
}
