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
@Table(name = TraceabilityTableNames.USE_CASE_FLOW_STEP)
@Getter
@Setter
@NoArgsConstructor
public class UseCaseFlowStepJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "flow_id", nullable = false)
    private UUID flowId;

    @Column(name = "step_type", nullable = false, length = 50)
    private String stepType;

    @Column(name = "screen_context_id")
    private UUID screenContextId;

    @Column(name = "content_json", columnDefinition = "text")
    private String contentJson;

    @Column(name = "next_screen_id")
    private UUID nextScreenId;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
