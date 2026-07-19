package com.company.scopery.modules.integrationhub.deadletter.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = IntegrationTableNames.DEAD_LETTER)
@Getter
@Setter
@NoArgsConstructor
public class DeadLetterEventJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "source_type", nullable = false) private String sourceType;
    @Column(name = "source_id") private UUID sourceId;
    @Column(name = "event_type") private String eventType;
    @Column(name = "payload_reference") private String payloadReference;
    @Column(name = "failure_code", nullable = false) private String failureCode;
    @Column(name = "failure_message", columnDefinition = "text") private String failureMessage;
    @Column(name = "attempt_count", nullable = false) private Integer attemptCount = 0;
    @Column(nullable = false) private String status;
    @Column(name = "last_attempt_at") private Instant lastAttemptAt;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "resolved_by") private UUID resolvedBy;
    @Version private Integer version;
}
