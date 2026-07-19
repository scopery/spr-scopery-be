package com.company.scopery.modules.quality.coverage.application.response;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverage;
import java.time.Instant; import java.util.UUID;
public record TestCaseCoverageResponse(UUID id, UUID projectId, UUID testCaseId, String targetType, UUID targetId, String coverageType, Instant createdAt) {
    public static TestCaseCoverageResponse from(TestCaseCoverage e) {
        return new TestCaseCoverageResponse(e.id(), e.projectId(), e.testCaseId(), e.targetType(), e.targetId(), e.coverageType().name(), e.createdAt());
    }
}
