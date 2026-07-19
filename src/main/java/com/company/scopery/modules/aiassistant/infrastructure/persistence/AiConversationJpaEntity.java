package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
import jakarta.persistence.*;
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
    name = AiAssistantTableNames.CONVERSATION,
    indexes = {
        @Index(name = "idx_aiassistant_conversation_workspace_id", columnList = "workspace_id"),
        @Index(name = "idx_aiassistant_conversation_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_aiassistant_conversation_status", columnList = "status"),
        @Index(name = "idx_aiassistant_conversation_last_message_at", columnList = "last_message_at")
    }
)
public class AiConversationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "owner_user_id", nullable = false, updatable = false)
    private UUID ownerUserId;

    @Column(name = "conversation_type", nullable = false, length = 50)
    private String conversationType;

    @Column(name = "capability_level", nullable = false, length = 50)
    private String capabilityLevel;

    @Column(name = "assistant_agent_id")
    private UUID assistantAgentId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "title_source", length = 50)
    private String titleSource;

    @Column(name = "retention_policy_code", length = 100)
    private String retentionPolicyCode;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "last_memory_summary_version")
    private Integer lastMemorySummaryVersion;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
