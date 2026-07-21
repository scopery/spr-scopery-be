package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.STEP)
public class AiActionStepJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;

    @Column(name = "tool_code", nullable = false, length = 100)
    private String toolCode;

    @Column(name = "tool_version", nullable = false, length = 20)
    private String toolVersion;

    @Column(name = "input_schema_code", length = 100)
    private String inputSchemaCode;

    @Column(name = "input_schema_version")
    private int inputSchemaVersion;

    @Column(name = "input_hash", length = 200)
    private String inputHash;

    @Column(name = "target_entity_type", length = 100)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    @Column(name = "expected_target_version_token", length = 200)
    private String expectedTargetVersionToken;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "execution_mode", length = 40)
    private String executionMode;

    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "depends_on_step_ids", columnDefinition = "jsonb")
    private String dependsOnStepIds;
}
