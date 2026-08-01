package com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = TraceabilityTableNames.FUNCTION_COMMUNICATION)
@Getter
@Setter
@NoArgsConstructor
public class FunctionCommunicationJpaEntity {

    @EmbeddedId
    private FunctionCommunicationId id;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
}
