package com.company.scopery.modules.eventregistry.eventdefinition.domain.model;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;

import java.time.Instant;
import java.util.UUID;

public class EventDefinition {

    public static final int INITIAL_VERSION = 1;

    private final UUID id;
    private final EventDefinitionCode code;
    private String name;
    private final SourceSystemCode sourceSystem;
    private final EventKey eventKey;
    private String description;
    private String inputSchema;
    private String outputSchema;
    private EventDefinitionStatus status;
    private int eventVersion;
    private String samplePayloadJson;
    private EventDataClassification dataClassification;
    private String ownerModule;
    private boolean systemEvent;
    private Instant deprecatedAt;
    private UUID deprecatedBy;
    private UUID replacementEventDefinitionId;
    private final Instant createdAt;
    private Instant updatedAt;

    private EventDefinition(UUID id, EventDefinitionCode code, String name,
                            SourceSystemCode sourceSystem, EventKey eventKey,
                            String description, String inputSchema, String outputSchema,
                            EventDefinitionStatus status, int eventVersion, String samplePayloadJson,
                            EventDataClassification dataClassification, String ownerModule,
                            boolean systemEvent, Instant deprecatedAt, UUID deprecatedBy,
                            UUID replacementEventDefinitionId,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sourceSystem = sourceSystem;
        this.eventKey = eventKey;
        this.description = description;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.status = status;
        this.eventVersion = eventVersion;
        this.samplePayloadJson = samplePayloadJson;
        this.dataClassification = dataClassification;
        this.ownerModule = ownerModule;
        this.systemEvent = systemEvent;
        this.deprecatedAt = deprecatedAt;
        this.deprecatedBy = deprecatedBy;
        this.replacementEventDefinitionId = replacementEventDefinitionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventDefinition create(EventDefinitionCode code, String name,
                                         SourceSystemCode sourceSystem, EventKey eventKey,
                                         String description, String inputSchema, String outputSchema) {
        return create(code, name, sourceSystem, eventKey, description, inputSchema, outputSchema,
                null, null, true);
    }

    public static EventDefinition create(EventDefinitionCode code, String name,
                                         SourceSystemCode sourceSystem, EventKey eventKey,
                                         String description, String inputSchema, String outputSchema,
                                         EventDataClassification dataClassification,
                                         String ownerModule, boolean systemEvent) {
        validateName(name);
        Instant now = Instant.now();
        return new EventDefinition(UUID.randomUUID(), code, name, sourceSystem, eventKey,
                description, inputSchema, outputSchema, EventDefinitionStatus.ACTIVE, INITIAL_VERSION, null,
                dataClassification, ownerModule, systemEvent, null, null, null, now, now);
    }

    public static EventDefinition reconstitute(UUID id, EventDefinitionCode code, String name,
                                               SourceSystemCode sourceSystem, EventKey eventKey,
                                               String description, String inputSchema, String outputSchema,
                                               EventDefinitionStatus status, int eventVersion,
                                               String samplePayloadJson,
                                               Instant createdAt, Instant updatedAt) {
        return reconstitute(id, code, name, sourceSystem, eventKey, description, inputSchema, outputSchema,
                status, eventVersion, samplePayloadJson,
                null, null, true, null, null, null, createdAt, updatedAt);
    }

    public static EventDefinition reconstitute(UUID id, EventDefinitionCode code, String name,
                                               SourceSystemCode sourceSystem, EventKey eventKey,
                                               String description, String inputSchema, String outputSchema,
                                               EventDefinitionStatus status, int eventVersion,
                                               String samplePayloadJson,
                                               EventDataClassification dataClassification,
                                               String ownerModule, boolean systemEvent,
                                               Instant deprecatedAt, UUID deprecatedBy,
                                               UUID replacementEventDefinitionId,
                                               Instant createdAt, Instant updatedAt) {
        return new EventDefinition(id, code, name, sourceSystem, eventKey,
                description, inputSchema, outputSchema, status, eventVersion, samplePayloadJson,
                dataClassification, ownerModule, systemEvent, deprecatedAt, deprecatedBy,
                replacementEventDefinitionId, createdAt, updatedAt);
    }

    public void update(String name, String description, String inputSchema, String outputSchema) {
        ensureNotDeprecatedForMutation();
        validateName(name);
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.updatedAt = Instant.now();
    }

    public void updateGovernance(EventDataClassification dataClassification, String ownerModule) {
        ensureNotDeprecatedForMutation();
        if (dataClassification != null) {
            this.dataClassification = dataClassification;
        }
        if (ownerModule != null && !ownerModule.isBlank()) {
            this.ownerModule = ownerModule.trim();
        }
        this.updatedAt = Instant.now();
    }

    public void updateSamplePayload(String samplePayloadJson) {
        ensureNotDeprecatedForMutation();
        this.samplePayloadJson = samplePayloadJson;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == EventDefinitionStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated event definition cannot be activated again");
        }
        this.status = EventDefinitionStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status == EventDefinitionStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated event definition cannot be deactivated");
        }
        this.status = EventDefinitionStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deprecate(UUID replacementEventDefinitionId, UUID deprecatedBy) {
        if (this.status == EventDefinitionStatus.DEPRECATED) {
            throw new IllegalStateException("Event definition is already deprecated");
        }
        this.status = EventDefinitionStatus.DEPRECATED;
        this.deprecatedAt = Instant.now();
        this.deprecatedBy = deprecatedBy;
        this.replacementEventDefinitionId = replacementEventDefinitionId;
        this.updatedAt = Instant.now();
    }

    private void ensureNotDeprecatedForMutation() {
        if (this.status == EventDefinitionStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated event definition cannot be updated");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event definition name is required");
        }
    }

    public UUID id() { return id; }
    public EventDefinitionCode code() { return code; }
    public String name() { return name; }
    public SourceSystemCode sourceSystem() { return sourceSystem; }
    public EventKey eventKey() { return eventKey; }
    public String description() { return description; }
    public String inputSchema() { return inputSchema; }
    public String outputSchema() { return outputSchema; }
    public EventDefinitionStatus status() { return status; }
    public int eventVersion() { return eventVersion; }
    public String samplePayloadJson() { return samplePayloadJson; }
    public EventDataClassification dataClassification() { return dataClassification; }
    public String ownerModule() { return ownerModule; }
    public boolean systemEvent() { return systemEvent; }
    public Instant deprecatedAt() { return deprecatedAt; }
    public UUID deprecatedBy() { return deprecatedBy; }
    public UUID replacementEventDefinitionId() { return replacementEventDefinitionId; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
