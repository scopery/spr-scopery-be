package com.company.scopery.modules.aiaction.plan.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiActionPreview {

    private final UUID id;
    private final UUID planId;
    private String previewHash;
    private String maskedDiffJson;
    private String warningsJson;
    private String baselineImpact;
    private boolean externalSideEffect;
    private Instant validUntil;
    private final Instant createdAt;

    private AiActionPreview(UUID id, UUID planId, String previewHash, String maskedDiffJson,
                             String warningsJson, String baselineImpact, boolean externalSideEffect,
                             Instant validUntil, Instant createdAt) {
        this.id = id;
        this.planId = planId;
        this.previewHash = previewHash;
        this.maskedDiffJson = maskedDiffJson;
        this.warningsJson = warningsJson;
        this.baselineImpact = baselineImpact;
        this.externalSideEffect = externalSideEffect;
        this.validUntil = validUntil;
        this.createdAt = createdAt;
    }

    public static AiActionPreview create(UUID planId, String previewHash, String maskedDiffJson,
                                          String warningsJson, String baselineImpact,
                                          boolean externalSideEffect, Instant validUntil) {
        return new AiActionPreview(UUID.randomUUID(), planId, previewHash, maskedDiffJson,
                warningsJson, baselineImpact, externalSideEffect, validUntil, Instant.now());
    }

    public static AiActionPreview reconstitute(UUID id, UUID planId, String previewHash,
                                                String maskedDiffJson, String warningsJson,
                                                String baselineImpact, boolean externalSideEffect,
                                                Instant validUntil, Instant createdAt) {
        return new AiActionPreview(id, planId, previewHash, maskedDiffJson, warningsJson,
                baselineImpact, externalSideEffect, validUntil, createdAt);
    }

    public boolean isExpired() {
        return validUntil != null && Instant.now().isAfter(validUntil);
    }

    public UUID id()                    { return id; }
    public UUID planId()                { return planId; }
    public String previewHash()         { return previewHash; }
    public String maskedDiffJson()      { return maskedDiffJson; }
    public String warningsJson()        { return warningsJson; }
    public String baselineImpact()      { return baselineImpact; }
    public boolean externalSideEffect() { return externalSideEffect; }
    public Instant validUntil()         { return validUntil; }
    public Instant createdAt()          { return createdAt; }
}
