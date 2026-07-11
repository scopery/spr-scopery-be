package com.company.scopery.modules.eventregistry.eventdefinition.domain.model;

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
    private final Instant createdAt;
    private Instant updatedAt;

    private EventDefinition(UUID id, EventDefinitionCode code, String name,
                            SourceSystemCode sourceSystem, EventKey eventKey,
                            String description, String inputSchema, String outputSchema,
                            EventDefinitionStatus status, int eventVersion, String samplePayloadJson,
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventDefinition create(EventDefinitionCode code, String name,
                                         SourceSystemCode sourceSystem, EventKey eventKey,
                                         String description, String inputSchema, String outputSchema) {
        validateName(name);
        Instant now = Instant.now();
        return new EventDefinition(UUID.randomUUID(), code, name, sourceSystem, eventKey,
                description, inputSchema, outputSchema, EventDefinitionStatus.ACTIVE, INITIAL_VERSION, null, now, now);
    }

    public static EventDefinition reconstitute(UUID id, EventDefinitionCode code, String name,
                                               SourceSystemCode sourceSystem, EventKey eventKey,
                                               String description, String inputSchema, String outputSchema,
                                               EventDefinitionStatus status, int eventVersion,
                                               String samplePayloadJson,
                                               Instant createdAt, Instant updatedAt) {
        return new EventDefinition(id, code, name, sourceSystem, eventKey,
                description, inputSchema, outputSchema, status, eventVersion, samplePayloadJson,
                createdAt, updatedAt);
    }

    public void update(String name, String description, String inputSchema, String outputSchema) {
        validateName(name);
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.updatedAt = Instant.now();
    }

    public void updateSamplePayload(String samplePayloadJson) {
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
        this.status = EventDefinitionStatus.INACTIVE;
        this.updatedAt = Instant.now();
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
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
