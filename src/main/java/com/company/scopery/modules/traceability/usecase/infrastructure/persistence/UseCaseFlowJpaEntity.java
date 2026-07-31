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
@Table(name = TraceabilityTableNames.USE_CASE_FLOW)
@Getter
@Setter
@NoArgsConstructor
public class UseCaseFlowJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "use_case_id", nullable = false)
    private UUID useCaseId;

    @Column(name = "flow_type", nullable = false, length = 30)
    private String flowType;

    @Column(length = 255)
    private String name;

    @Column(name = "source_step_id")
    private UUID sourceStepId;

    @Column(name = "condition_text", columnDefinition = "text")
    private String conditionText;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
