package com.company.scopery.modules.specpack.clarification.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
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
        name = SpecPackTableNames.SPEC_PACK_CLARIFICATION,
        indexes = {
                @Index(name = "idx_spec_pack_clarification_session_id", columnList = "session_id"),
                @Index(name = "idx_spec_pack_clarification_status", columnList = "status"),
                @Index(name = "idx_spec_pack_clarification_priority", columnList = "priority")
        }
)
public class SpecPackClarificationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "code", nullable = false, length = 200)
    private String code;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "priority", nullable = false, length = 50)
    private String priority;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Override
    public UUID getId() { return id; }
}
