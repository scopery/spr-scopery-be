package com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.SCREEN_COMPONENT)
@Getter
@Setter
@NoArgsConstructor
public class ScreenComponentJpaEntity {

    @EmbeddedId
    private ScreenComponentId id;

    @Column(name = "section_id")
    private UUID sectionId;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
}
