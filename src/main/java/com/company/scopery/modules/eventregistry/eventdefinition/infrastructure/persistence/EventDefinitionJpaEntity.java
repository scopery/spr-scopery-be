package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = EventRegistryTableNames.EVENT_DEFINITION,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_app_event_definition_code",
                        columnNames = {"code"}),
                @UniqueConstraint(name = "uq_app_event_definition_source_system_event_key",
                        columnNames = {"source_system", "event_key"})
        },
        indexes = {
                @Index(name = "idx_app_event_definition_code",          columnList = "code"),
                @Index(name = "idx_app_event_definition_source_system", columnList = "source_system"),
                @Index(name = "idx_app_event_definition_event_key",     columnList = "event_key"),
                @Index(name = "idx_app_event_definition_status",        columnList = "status")
        }
)
public class EventDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 150, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "source_system", nullable = false, length = 100, updatable = false)
    private String sourceSystem;

    @Column(name = "event_key", nullable = false, length = 150, updatable = false)
    private String eventKey;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "input_schema", columnDefinition = "TEXT")
    private String inputSchema;

    @Column(name = "output_schema", columnDefinition = "TEXT")
    private String outputSchema;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "event_version", nullable = false)
    private int eventVersion = 1;

    @Column(name = "sample_payload_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String samplePayloadJson;

    @Column(name = "data_classification", length = 50)
    private String dataClassification;

    @Column(name = "owner_module", length = 100)
    private String ownerModule;

    @Column(name = "is_system_event", nullable = false)
    private boolean systemEvent = true;

    @Column(name = "deprecated_at")
    private java.time.Instant deprecatedAt;

    @Column(name = "deprecated_by")
    private UUID deprecatedBy;

    @Column(name = "replacement_event_definition_id")
    private UUID replacementEventDefinitionId;
}
