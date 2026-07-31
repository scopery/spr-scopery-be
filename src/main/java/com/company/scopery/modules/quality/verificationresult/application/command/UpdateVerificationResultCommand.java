package com.company.scopery.modules.quality.verificationresult.application.command;
import java.math.BigDecimal; import java.util.UUID;
public record UpdateVerificationResultCommand(UUID projectId, UUID testRunId, UUID resultId,
        String resultStatus, BigDecimal actualValue, String actualValueUnit, String actualResultJson,
        String evidenceReference, UUID executedById, UUID defectId, String comment, int version) {}
