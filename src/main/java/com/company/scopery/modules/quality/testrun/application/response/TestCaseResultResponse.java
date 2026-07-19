package com.company.scopery.modules.quality.testrun.application.response;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult; import java.util.UUID;
public record TestCaseResultResponse(UUID id, UUID testRunId, UUID testCaseId, String resultStatus, UUID defectId) {
    public static TestCaseResultResponse from(TestCaseResult e) { return new TestCaseResultResponse(e.id(), e.testRunId(), e.testCaseId(), e.resultStatus().name(), e.defectId()); }
}
