package com.company.scopery.modules.quality.testrun.application.response;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult;
import java.time.Instant; import java.util.UUID;
public record TestCaseResultResponse(UUID id, UUID testRunId, TestCaseSummary testCase, String resultStatus,
        UUID defectId, String comment, UUID assigneeId, Instant executedAt, Instant createdAt, Long version) {
    public record TestCaseSummary(UUID id, String code, String title) {}
    public static TestCaseResultResponse from(TestCaseResult e) {
        return new TestCaseResultResponse(e.id(), e.testRunId(), new TestCaseSummary(e.testCaseId(), null, null),
                e.resultStatus().name(), e.defectId(), e.comment(), e.assigneeId(), e.executedAt(), e.createdAt(), (long) e.version());
    }
}
