package com.company.scopery.modules.elicitation.question.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = ElicitationTableNames.QUESTION,
        indexes = {
                @Index(name = "idx_elicitation_question_session", columnList = "session_id"),
                @Index(name = "idx_elicitation_question_status", columnList = "session_id, status"),
                @Index(name = "idx_elicitation_question_parent", columnList = "parent_question_id")
        }
)
public class ElicitationQuestionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "clarity_level", length = 50)
    private String clarityLevel;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "parent_question_id")
    private UUID parentQuestionId;

    @Column(name = "answered_at")
    private Instant answeredAt;

    @Override
    public UUID getId() { return id; }
}
