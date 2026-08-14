package com.company.scopery.modules.traceability.fieldvalidation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.SCREEN_FIELD_VALIDATION)
@Getter
@Setter
@NoArgsConstructor
public class RegistryScreenFieldValidationJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "field_id", nullable = false)
    private UUID fieldId;

    @Column(name = "mode_id")
    private UUID modeId;

    @Column(name = "rule_type_id", nullable = false)
    private UUID ruleTypeId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rule_param_json", columnDefinition = "jsonb")
    private String ruleParamJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "condition_json", columnDefinition = "jsonb")
    private String conditionJson;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(columnDefinition = "text")
    private String remark;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
