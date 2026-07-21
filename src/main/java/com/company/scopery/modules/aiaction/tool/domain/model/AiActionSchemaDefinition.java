package com.company.scopery.modules.aiaction.tool.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiActionSchemaDefinition {

    private final UUID id;
    private final String schemaCode;
    private final int schemaVersion;
    private final String schemaJson;
    private String status;
    private final Instant createdAt;

    private AiActionSchemaDefinition(UUID id, String schemaCode, int schemaVersion,
                                      String schemaJson, String status, Instant createdAt) {
        this.id = id;
        this.schemaCode = schemaCode;
        this.schemaVersion = schemaVersion;
        this.schemaJson = schemaJson;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AiActionSchemaDefinition create(String schemaCode, int schemaVersion, String schemaJson) {
        return new AiActionSchemaDefinition(UUID.randomUUID(), schemaCode, schemaVersion,
                schemaJson, "ACTIVE", Instant.now());
    }

    public static AiActionSchemaDefinition reconstitute(UUID id, String schemaCode, int schemaVersion,
                                                         String schemaJson, String status, Instant createdAt) {
        return new AiActionSchemaDefinition(id, schemaCode, schemaVersion, schemaJson, status, createdAt);
    }

    public UUID id()            { return id; }
    public String schemaCode()  { return schemaCode; }
    public int schemaVersion()  { return schemaVersion; }
    public String schemaJson()  { return schemaJson; }
    public String status()      { return status; }
    public Instant createdAt()  { return createdAt; }
}
