package com.company.scopery.modules.quality.verificationresult.domain.model;
import com.company.scopery.modules.quality.verificationresult.domain.enums.VerificationResultStatus;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record VerificationCaseResult(
        UUID id, UUID projectId, UUID testRunId, UUID verificationCaseId,
        VerificationResultStatus resultStatus,
        BigDecimal actualValue, String actualValueUnit, String actualResultJson,
        String evidenceReference, Instant executedAt, UUID executedById,
        UUID defectId, String comment,
        int version, Instant createdAt, Instant updatedAt) {

    public static VerificationCaseResult create(UUID projectId, UUID testRunId, UUID verificationCaseId,
            VerificationResultStatus resultStatus, BigDecimal actualValue, String actualValueUnit,
            String actualResultJson, String evidenceReference, UUID executedById, UUID defectId, String comment) {
        Instant now = Instant.now();
        return new VerificationCaseResult(UUID.randomUUID(), projectId, testRunId, verificationCaseId,
                resultStatus != null ? resultStatus : VerificationResultStatus.NOT_RUN,
                actualValue, actualValueUnit, actualResultJson, evidenceReference,
                resultStatus != null && resultStatus != VerificationResultStatus.NOT_RUN ? now : null,
                executedById, defectId, comment, -1, now, now);
    }

    public VerificationCaseResult withDefect(UUID defectId) {
        return new VerificationCaseResult(id, projectId, testRunId, verificationCaseId, resultStatus,
                actualValue, actualValueUnit, actualResultJson, evidenceReference, executedAt, executedById,
                defectId, comment, version, createdAt, Instant.now());
    }
    public VerificationCaseResult update(VerificationResultStatus resultStatus, BigDecimal actualValue,
            String actualValueUnit, String actualResultJson, String evidenceReference,
            UUID executedById, UUID defectId, String comment) {
        return new VerificationCaseResult(id, projectId, testRunId, verificationCaseId,
                resultStatus != null ? resultStatus : this.resultStatus,
                actualValue, actualValueUnit, actualResultJson, evidenceReference,
                resultStatus != null && resultStatus != VerificationResultStatus.NOT_RUN ? Instant.now() : this.executedAt,
                executedById, defectId, comment, version, createdAt, Instant.now());
    }
}
