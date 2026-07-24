package com.company.scopery.modules.aiaction.request.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.REQUEST)
public class AiActionRequestJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "initiated_by_user_id", nullable = false)
    private UUID initiatedByUserId;

    @Column(name = "origin_type", nullable = false, length = 30)
    private String originType;

    @Column(name = "origin_conversation_id", length = 200)
    private String originConversationId;

    @Column(name = "origin_message_id", length = 200)
    private String originMessageId;

    @Column(name = "origin_turn_id", length = 200)
    private String originTurnId;

    @Column(name = "origin_suggestion_ref", length = 200)
    private String originSuggestionRef;

    @Column(name = "legacy_phase21_suggestion_id", length = 200)
    private String legacyPhase21SuggestionId;

    @Column(name = "intent_summary", nullable = false, length = 2000)
    private String intentSummary;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "idempotency_key", nullable = false, length = 200)
    private String idempotencyKey;

    @Column(name = "request_hash", length = 200)
    private String requestHash;

    @Column(name = "latest_plan_id")
    private UUID latestPlanId;

    @Column(name = "requested_actions_json", columnDefinition = "TEXT")
    private String requestedActionsJson;
}
