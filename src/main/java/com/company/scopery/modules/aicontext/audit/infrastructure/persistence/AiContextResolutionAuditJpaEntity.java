package com.company.scopery.modules.aicontext.audit.infrastructure.persistence;

import com.company.scopery.modules.aicontext.shared.constant.AiContextTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable audit record — does NOT extend AuditableJpaEntity.
 * This entity captures a point-in-time resolution event with only a {@code resolved_at} timestamp.
 * All columns are non-updatable.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiContextTableNames.RESOLUTION_AUDIT)
public class AiContextResolutionAuditJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "policy_id", updatable = false)
    private UUID policyId;

    @Column(name = "document_id", updatable = false)
    private UUID documentId;

    @Column(name = "actor_id", updatable = false)
    private UUID actorId;

    @Column(name = "token_count", updatable = false)
    private Integer tokenCount;

    @Column(name = "block_count", updatable = false)
    private Integer blockCount;

    @Column(name = "status", nullable = false, length = 50, updatable = false)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT", updatable = false)
    private String errorMessage;

    @Column(name = "resolved_at", nullable = false, updatable = false)
    private Instant resolvedAt;
}
