package com.company.scopery.modules.traceability.functionapi.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = TraceabilityTableNames.FUNCTION_API)
@Getter
@Setter
@NoArgsConstructor
public class FunctionApiJpaEntity {

    @EmbeddedId
    private FunctionApiId id;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
}
