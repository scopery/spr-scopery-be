package com.company.scopery.modules.traceability.commspec.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.COMMUNICATION_SPECIFICATION)
@Getter
@Setter
@NoArgsConstructor
public class CommunicationSpecificationJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "application_id", nullable = false)
    private UUID applicationId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "trigger_name")
    private String triggerName;
    @Column(name = "trigger_key", length = 100)
    private String triggerKey;
    @Column(name = "trigger_timing", length = 50)
    private String triggerTiming;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "condition_json", columnDefinition = "jsonb")
    private String conditionJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suppression_condition_json", columnDefinition = "jsonb")
    private String suppressionConditionJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delivery_policy_json", columnDefinition = "jsonb")
    private String deliveryPolicyJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "in_app_contract_json", columnDefinition = "jsonb")
    private String inAppContractJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "email_contract_json", columnDefinition = "jsonb")
    private String emailContractJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recipients_json", columnDefinition = "jsonb")
    private String recipientsJson;
    @Column(name = "owner_id")
    private UUID ownerId;
    @Version
    private Integer version;
    @Column(name = "archived_at")
    private Instant archivedAt;
}
