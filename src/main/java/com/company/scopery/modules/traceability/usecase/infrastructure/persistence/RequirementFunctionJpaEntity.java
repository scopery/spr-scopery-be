package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = TraceabilityTableNames.REQUIREMENT_FUNCTION)
@Getter
@Setter
@NoArgsConstructor
public class RequirementFunctionJpaEntity {

    @EmbeddedId
    private RequirementFunctionId id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 255)
    private String createdBy;

    public RequirementFunctionJpaEntity(RequirementFunctionId id) {
        this.id = id;
        this.createdAt = Instant.now();
        this.createdBy = "SYSTEM";
    }
}
