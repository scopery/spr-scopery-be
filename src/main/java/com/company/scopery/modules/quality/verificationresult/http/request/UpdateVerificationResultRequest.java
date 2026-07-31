package com.company.scopery.modules.quality.verificationresult.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal; import java.util.UUID;
public record UpdateVerificationResultRequest(
        @Schema(allowableValues={"PASSED","FAILED","BLOCKED","SKIPPED","NOT_RUN"}) String resultStatus,
        BigDecimal actualValue, String actualValueUnit,
        String actualResultJson, String evidenceReference,
        UUID executedById, UUID defectId, String comment, int version) {}
