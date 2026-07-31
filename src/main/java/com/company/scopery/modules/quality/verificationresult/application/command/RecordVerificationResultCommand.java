package com.company.scopery.modules.quality.verificationresult.application.command;
import java.math.BigDecimal; import java.util.UUID;
public record RecordVerificationResultCommand(UUID projectId, UUID testRunId, UUID verificationCaseId,
        String resultStatus, BigDecimal actualValue, String actualValueUnit, String actualResultJson,
        String evidenceReference, UUID executedById, UUID defectId, String comment) {}
