package com.company.scopery.modules.traceability.funcitemprop.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.FUNCTIONAL_ITEM_CUSTOM_PROPERTY)
@Getter
@Setter
@NoArgsConstructor
public class FunctionalItemCustomPropertyJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "functional_item_id", nullable = false)
    private UUID functionalItemId;

    @Column(name = "prop_key", nullable = false)
    private String propKey;

    @Column(name = "prop_value", columnDefinition = "text")
    private String propValue;

    @Column(name = "field_type", nullable = false)
    private String fieldType;
}
