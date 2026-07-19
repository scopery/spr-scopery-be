package com.company.scopery.modules.quality.testrun.domain.model;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus;
import java.time.Instant; import java.util.UUID;
public record TestCaseResult(UUID id, UUID projectId, UUID testRunId, UUID testCaseId, TestResultStatus resultStatus,
        String actualResult, String evidenceReference, Instant executedAt, UUID executedBy, UUID defectId,
        int version, Instant createdAt, Instant updatedAt) {
    public static TestCaseResult create(UUID projectId, UUID testRunId, UUID testCaseId, TestResultStatus status,
                                        String actualResult, UUID executedBy) {
        Instant now = Instant.now();
        return new TestCaseResult(UUID.randomUUID(), projectId, testRunId, testCaseId, status, actualResult, null, now, executedBy, null, 0, now, now);
    }
    public TestCaseResult withDefect(UUID defectId) {
        return new TestCaseResult(id, projectId, testRunId, testCaseId, resultStatus, actualResult, evidenceReference, executedAt, executedBy, defectId, version, createdAt, Instant.now());
    }
}
