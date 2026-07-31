package com.company.scopery.modules.quality.verificationresult.application.response;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResult;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record VerificationCaseResultResponse(
        UUID id, UUID projectId, UUID testRunId, UUID verificationCaseId,
        String resultStatus, BigDecimal actualValue, String actualValueUnit,
        String actualResultJson, String evidenceReference,
        Instant executedAt, UUID executedById, UUID defectId, String comment,
        int version, Instant createdAt, Instant updatedAt) {
    public static VerificationCaseResultResponse from(VerificationCaseResult r) {
        return new VerificationCaseResultResponse(r.id(), r.projectId(), r.testRunId(), r.verificationCaseId(),
                r.resultStatus().name(), r.actualValue(), r.actualValueUnit(), r.actualResultJson(), r.evidenceReference(),
                r.executedAt(), r.executedById(), r.defectId(), r.comment(),
                r.version(), r.createdAt(), r.updatedAt());
    }
}
