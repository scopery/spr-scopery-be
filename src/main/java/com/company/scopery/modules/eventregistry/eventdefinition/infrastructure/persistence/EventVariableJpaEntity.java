package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = EventRegistryTableNames.EVENT_VARIABLE,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_app_event_variable_path",
                        columnNames = {"event_definition_id", "variable_path"})
        },
        indexes = {
                @Index(name = "idx_app_event_variable_definition_id", columnList = "event_definition_id"),
                @Index(name = "idx_app_event_variable_path",          columnList = "variable_path")
        }
)
public class EventVariableJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_definition_id", nullable = false, updatable = false)
    private UUID eventDefinitionId;

    @Column(name = "variable_path", nullable = false, length = 255)
    private String variablePath;

    @Column(name = "variable_label", nullable = false, length = 255)
    private String variableLabel;

    @Column(name = "variable_type", nullable = false, length = 50)
    private String variableType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "sensitive", nullable = false)
    private boolean sensitive;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "example_value", columnDefinition = "TEXT")
    private String exampleValue;
}
