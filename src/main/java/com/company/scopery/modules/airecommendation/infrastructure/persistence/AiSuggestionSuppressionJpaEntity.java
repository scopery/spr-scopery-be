package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.SUPPRESSION,
        indexes = {
                @Index(name = "idx_ai_recommendation_suppression_workspace_id",    columnList = "workspace_id"),
                @Index(name = "idx_ai_recommendation_suppression_project_id",      columnList = "project_id"),
                @Index(name = "idx_ai_recommendation_suppression_actor_id",        columnList = "actor_id"),
                @Index(name = "idx_ai_recommendation_suppression_key",             columnList = "workspace_id,project_id,actor_id,suppression_key"),
                @Index(name = "idx_ai_recommendation_suppression_active",          columnList = "active"),
                @Index(name = "idx_ai_recommendation_suppression_expires_at",      columnList = "expires_at"),
                @Index(name = "idx_ai_recommendation_suppression_target_entity",   columnList = "workspace_id,project_id,actor_id,suggestion_type,target_entity_type,target_entity_id")
        }
)
public class AiSuggestionSuppressionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "scope_type", nullable = false, length = 100)
    private String scopeType;

    @Column(name = "scope_key", length = 500)
    private String scopeKey;

    @Column(name = "suppression_key", nullable = false, length = 500)
    private String suppressionKey;

    @Column(name = "target_entity_type", length = 100)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    @Column(name = "suggestion_type", length = 100)
    private String suggestionType;

    @Column(name = "pack_code", length = 100)
    private String packCode;

    @Column(name = "reason_code", length = 100)
    private String reasonCode;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
