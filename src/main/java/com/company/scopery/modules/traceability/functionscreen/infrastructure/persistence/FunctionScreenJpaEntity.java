package com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.FUNCTION_SCREEN)
@Getter
@Setter
@NoArgsConstructor
public class FunctionScreenJpaEntity {

    @EmbeddedId
    private FunctionScreenId id;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
}
