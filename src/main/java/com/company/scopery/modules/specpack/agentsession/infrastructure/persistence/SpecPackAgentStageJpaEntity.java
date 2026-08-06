package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = SpecPackTableNames.SPEC_PACK_AGENT_STAGE,
        indexes = {
                @Index(name = "idx_spec_pack_agent_stage_session_id", columnList = "session_id"),
                @Index(name = "idx_spec_pack_agent_stage_session_stage", columnList = "session_id,stage_code")
        }
)
public class SpecPackAgentStageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "stage_code", nullable = false, length = 50)
    private String stageCode;

    @Column(name = "stage_status", nullable = false, length = 50)
    private String stageStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_json", columnDefinition = "jsonb")
    private String resultJson;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
