package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = TraceabilityTableNames.SPEC_DOC_SCREEN)
@Getter
@Setter
@NoArgsConstructor
public class SpecDocScreenJpaEntity {

    @EmbeddedId
    private SpecDocScreenId id;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
