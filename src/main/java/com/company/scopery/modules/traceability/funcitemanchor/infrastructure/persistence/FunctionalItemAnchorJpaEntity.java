package com.company.scopery.modules.traceability.funcitemanchor.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.FUNCTIONAL_ITEM_ANCHOR)
@Getter
@Setter
@NoArgsConstructor
public class FunctionalItemAnchorJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "functional_item_id", nullable = false)
    private UUID functionalItemId;

    @Column(name = "node_type", nullable = false)
    private String nodeType;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(columnDefinition = "text")
    private String note;
}
