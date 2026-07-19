package com.company.scopery.modules.aiassistant.infrastructure.persistence;

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
    name = AiAssistantTableNames.ANSWER_FEEDBACK,
    indexes = {
        @Index(name = "idx_aiassistant_answer_feedback_message_id", columnList = "message_id"),
        @Index(name = "idx_aiassistant_answer_feedback_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_aiassistant_answer_feedback_actor_id", columnList = "actor_id")
    }
)
public class AiAnswerFeedbackJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "actor_id", nullable = false, updatable = false)
    private UUID actorId;

    @Column(name = "rating", nullable = false, length = 50)
    private String rating;

    @Column(name = "reason_code", length = 100)
    private String reasonCode;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
