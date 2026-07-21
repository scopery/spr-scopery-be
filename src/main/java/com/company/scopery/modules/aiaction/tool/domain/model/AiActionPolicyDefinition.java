package com.company.scopery.modules.aiaction.tool.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiActionPolicyDefinition {

    private final UUID id;
    private final String policyCode;
    private final int policyVersion;
    private final String configJson;
    private String status;
    private final Instant createdAt;

    private AiActionPolicyDefinition(UUID id, String policyCode, int policyVersion,
                                      String configJson, String status, Instant createdAt) {
        this.id = id;
        this.policyCode = policyCode;
        this.policyVersion = policyVersion;
        this.configJson = configJson;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AiActionPolicyDefinition create(String policyCode, int policyVersion, String configJson) {
        return new AiActionPolicyDefinition(UUID.randomUUID(), policyCode, policyVersion,
                configJson, "ACTIVE", Instant.now());
    }

    public static AiActionPolicyDefinition reconstitute(UUID id, String policyCode, int policyVersion,
                                                         String configJson, String status, Instant createdAt) {
        return new AiActionPolicyDefinition(id, policyCode, policyVersion, configJson, status, createdAt);
    }

    public UUID id()            { return id; }
    public String policyCode()  { return policyCode; }
    public int policyVersion()  { return policyVersion; }
    public String configJson()  { return configJson; }
    public String status()      { return status; }
    public Instant createdAt()  { return createdAt; }
}
