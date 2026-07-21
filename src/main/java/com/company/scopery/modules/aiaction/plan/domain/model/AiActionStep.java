package com.company.scopery.modules.aiaction.plan.domain.model;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;

import java.util.List;
import java.util.UUID;

public class AiActionStep {

    private final UUID id;
    private final UUID planId;
    private final int ordinal;
    private final String toolCode;
    private final String toolVersion;
    private final String inputSchemaCode;
    private final int inputSchemaVersion;
    private final String inputHash;
    private final String targetEntityType;
    private final UUID targetEntityId;
    private final String expectedTargetVersionToken;
    private final AiActionRiskLevel riskLevel;
    private final AiActionExecutionMode executionMode;
    private final List<UUID> dependsOnStepIds;

    private AiActionStep(UUID id, UUID planId, int ordinal, String toolCode, String toolVersion,
                          String inputSchemaCode, int inputSchemaVersion, String inputHash,
                          String targetEntityType, UUID targetEntityId,
                          String expectedTargetVersionToken, AiActionRiskLevel riskLevel,
                          AiActionExecutionMode executionMode, List<UUID> dependsOnStepIds) {
        this.id = id;
        this.planId = planId;
        this.ordinal = ordinal;
        this.toolCode = toolCode;
        this.toolVersion = toolVersion;
        this.inputSchemaCode = inputSchemaCode;
        this.inputSchemaVersion = inputSchemaVersion;
        this.inputHash = inputHash;
        this.targetEntityType = targetEntityType;
        this.targetEntityId = targetEntityId;
        this.expectedTargetVersionToken = expectedTargetVersionToken;
        this.riskLevel = riskLevel;
        this.executionMode = executionMode;
        this.dependsOnStepIds = dependsOnStepIds == null ? List.of() : List.copyOf(dependsOnStepIds);
    }

    public static AiActionStep create(UUID planId, int ordinal, String toolCode, String toolVersion,
                                       String inputSchemaCode, int inputSchemaVersion, String inputHash,
                                       String targetEntityType, UUID targetEntityId,
                                       String expectedTargetVersionToken, AiActionRiskLevel riskLevel,
                                       AiActionExecutionMode executionMode, List<UUID> dependsOnStepIds) {
        return new AiActionStep(UUID.randomUUID(), planId, ordinal, toolCode, toolVersion,
                inputSchemaCode, inputSchemaVersion, inputHash,
                targetEntityType, targetEntityId, expectedTargetVersionToken,
                riskLevel, executionMode, dependsOnStepIds);
    }

    public static AiActionStep reconstitute(UUID id, UUID planId, int ordinal, String toolCode, String toolVersion,
                                             String inputSchemaCode, int inputSchemaVersion, String inputHash,
                                             String targetEntityType, UUID targetEntityId,
                                             String expectedTargetVersionToken, AiActionRiskLevel riskLevel,
                                             AiActionExecutionMode executionMode, List<UUID> dependsOnStepIds) {
        return new AiActionStep(id, planId, ordinal, toolCode, toolVersion, inputSchemaCode, inputSchemaVersion,
                inputHash, targetEntityType, targetEntityId, expectedTargetVersionToken,
                riskLevel, executionMode, dependsOnStepIds);
    }

    public UUID id()                               { return id; }
    public UUID planId()                           { return planId; }
    public int ordinal()                           { return ordinal; }
    public String toolCode()                       { return toolCode; }
    public String toolVersion()                    { return toolVersion; }
    public String inputSchemaCode()                { return inputSchemaCode; }
    public int inputSchemaVersion()                { return inputSchemaVersion; }
    public String inputHash()                      { return inputHash; }
    public String targetEntityType()               { return targetEntityType; }
    public UUID targetEntityId()                   { return targetEntityId; }
    public String expectedTargetVersionToken()     { return expectedTargetVersionToken; }
    public AiActionRiskLevel riskLevel()           { return riskLevel; }
    public AiActionExecutionMode executionMode()   { return executionMode; }
    public List<UUID> dependsOnStepIds()           { return dependsOnStepIds; }
}
