package com.company.scopery.modules.elicitation.round.infrastructure.persistence;

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
        name = ElicitationTableNames.ROUND,
        indexes = {
                @Index(name = "idx_elicitation_round_session", columnList = "session_id"),
                @Index(name = "idx_elicitation_round_status", columnList = "status")
        }
)
public class ElicitationRoundJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "questions_json", nullable = false, columnDefinition = "TEXT")
    private String questionsJson;

    @Column(name = "overall_clarity", length = 50)
    private String overallClarity;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "scope_snapshot_json", columnDefinition = "TEXT")
    private String scopeSnapshotJson;

    @Column(name = "should_continue")
    private Boolean shouldContinue;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;

    @Override
    public UUID getId() { return id; }
}
