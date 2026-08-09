package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = SpecPackTableNames.SPEC_PACK_AGENT_STAGE,
        indexes = {
                @Index(name = "idx_spec_pack_agent_stage_session_id", columnList = "session_id"),
                @Index(name = "idx_spec_pack_agent_stage_session_stage", columnList = "session_id,stage_code")
        }
)
public class SpecPackAgentStageJpaEntity extends AuditableJpaEntity {

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

    @Override
    public Object getId() {
        return id;
    }
}
