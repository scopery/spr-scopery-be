package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.USE_CASE_BUSINESS_RULE)
@Getter
@Setter
@NoArgsConstructor
public class UseCaseBusinessRuleJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "use_case_id", nullable = false)
    private UUID useCaseId;

    @Column(name = "rule_code", nullable = false, length = 50)
    private String ruleCode;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
