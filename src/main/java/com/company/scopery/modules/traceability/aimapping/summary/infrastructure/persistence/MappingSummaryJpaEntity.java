package com.company.scopery.modules.traceability.aimapping.summary.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiMappingTableNames.MAPPING_SUMMARY,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_mapping_summary_entity",
                columnNames = {"entity_type", "entity_id"}
        ),
        indexes = {
                @Index(name = "idx_ai_mapping_summary_entity", columnList = "entity_type, entity_id")
        }
)
public class MappingSummaryJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_version", nullable = false)
    private int entityVersion;

    @Column(name = "compact_text", nullable = false, columnDefinition = "TEXT")
    private String compactText;

    @Column(name = "structured_json", nullable = false, columnDefinition = "TEXT")
    private String structuredJson;

    @Column(name = "summary_hash", nullable = false, length = 64)
    private String summaryHash;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
}
