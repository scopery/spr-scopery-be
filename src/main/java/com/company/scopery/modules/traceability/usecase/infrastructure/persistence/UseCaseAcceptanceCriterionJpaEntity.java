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
@Table(name = TraceabilityTableNames.USE_CASE_ACCEPTANCE_CRITERION)
@Getter
@Setter
@NoArgsConstructor
public class UseCaseAcceptanceCriterionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "use_case_id", nullable = false)
    private UUID useCaseId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "given_text", columnDefinition = "text")
    private String givenText;

    @Column(name = "when_text", columnDefinition = "text")
    private String whenText;

    @Column(name = "then_text", columnDefinition = "text")
    private String thenText;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
