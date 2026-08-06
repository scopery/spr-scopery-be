package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

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
        name = SpecPackTableNames.SPEC_PACK_AGENT_SESSION,
        indexes = {
                @Index(name = "idx_spec_pack_agent_session_project_id", columnList = "project_id"),
                @Index(name = "idx_spec_pack_agent_session_spec_pack_id", columnList = "spec_pack_id"),
                @Index(name = "idx_spec_pack_agent_session_status", columnList = "status")
        }
)
public class SpecPackAgentSessionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "spec_pack_id", nullable = false, updatable = false)
    private UUID specPackId;

    @Column(name = "scope_package_id")
    private UUID scopePackageId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_stage_code", nullable = false, length = 50)
    private String currentStageCode;

    @Column(name = "readiness_status", nullable = false, length = 50)
    private String readinessStatus;

    @Override
    public UUID getId() { return id; }
}
