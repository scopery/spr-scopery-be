package com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = ElicitationTableNames.SUGGESTION,
        indexes = {
                @Index(name = "idx_elicitation_suggestion_round", columnList = "round_id")
        }
)
public class ElicitationSuggestionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "round_id", nullable = false, updatable = false)
    private UUID roundId;

    @Column(name = "overall_summary", columnDefinition = "TEXT")
    private String overallSummary;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "ai_raw_response", columnDefinition = "TEXT")
    private String aiRawResponse;

    @Override
    public UUID getId() { return id; }
}
