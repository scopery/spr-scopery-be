package com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.VALIDATION_RULE_TYPE)
@Getter
@Setter
@NoArgsConstructor
public class RegistryValidationRuleTypeJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "param_schema_json", columnDefinition = "jsonb")
    private String paramSchemaJson;

    @Column(name = "default_message", columnDefinition = "text")
    private String defaultMessage;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem;

    @Column(nullable = false)
    private String status;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Version
    private Integer version;
}
