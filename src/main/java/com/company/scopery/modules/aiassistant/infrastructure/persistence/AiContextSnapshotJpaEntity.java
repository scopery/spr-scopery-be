package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
    name = AiAssistantTableNames.CONTEXT_SNAPSHOT,
    indexes = {
        @Index(name = "idx_aiassistant_context_snapshot_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_aiassistant_context_snapshot_assistant_message_id", columnList = "assistant_message_id"),
        @Index(name = "idx_aiassistant_context_snapshot_context_hash", columnList = "context_hash"),
        @Index(name = "idx_aiassistant_context_snapshot_context_status", columnList = "context_status")
    }
)
public class AiContextSnapshotJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "assistant_message_id", nullable = false, updatable = false)
    private UUID assistantMessageId;

    @Column(name = "turn_id", nullable = false, updatable = false)
    private UUID turnId;

    @Column(name = "actor_id", nullable = false, updatable = false)
    private UUID actorId;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "route", length = 500)
    private String route;

    @Column(name = "page_code", length = 100)
    private String pageCode;

    @Column(name = "page_metadata_version")
    private Integer pageMetadataVersion;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "entity_version")
    private Long entityVersion;

    @Column(name = "selected_action_code", length = 100)
    private String selectedActionCode;

    @Column(name = "tab_code", length = 100)
    private String tabCode;

    @Column(name = "locale", length = 20)
    private String locale;

    @Column(name = "timezone", length = 100)
    private String timezone;

    @Column(name = "client_context_version", nullable = false)
    private int clientContextVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_visible_field_codes", columnDefinition = "jsonb")
    private String clientVisibleFieldCodes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_reported_action_codes", columnDefinition = "jsonb")
    private String clientReportedActionCodes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "server_visible_field_codes", columnDefinition = "jsonb")
    private String serverVisibleFieldCodes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "available_action_codes", columnDefinition = "jsonb")
    private String availableActionCodes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "disabled_action_reasons", columnDefinition = "jsonb")
    private String disabledActionReasons;

    @Column(name = "permission_signature", length = 500)
    private String permissionSignature;

    @Column(name = "client_context_hash", length = 64)
    private String clientContextHash;

    @Column(name = "context_hash", nullable = false, length = 64)
    private String contextHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "server_context", columnDefinition = "jsonb")
    private String serverContext;

    @Column(name = "context_status", nullable = false, length = 50)
    private String contextStatus;

    @Column(name = "invalidation_reason_code", length = 100)
    private String invalidationReasonCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "invalidated_at")
    private Instant invalidatedAt;
}
