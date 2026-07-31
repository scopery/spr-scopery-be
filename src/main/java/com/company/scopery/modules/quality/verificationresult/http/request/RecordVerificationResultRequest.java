package com.company.scopery.modules.quality.verificationresult.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal; import java.util.UUID;
public record RecordVerificationResultRequest(
        @NotNull UUID verificationCaseId,
        @Schema(allowableValues={"PASSED","FAILED","BLOCKED","SKIPPED","NOT_RUN"}) String resultStatus,
        BigDecimal actualValue, String actualValueUnit,
        String actualResultJson, String evidenceReference,
        UUID executedById, UUID defectId, String comment) {}
