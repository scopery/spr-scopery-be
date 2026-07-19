package com.company.scopery.modules.quality.coverage.domain.model;
import com.company.scopery.modules.quality.coverage.domain.enums.CoverageType;
import java.time.Instant; import java.util.UUID;
public record TestCaseCoverage(UUID id, UUID projectId, UUID testCaseId, String targetType, UUID targetId, CoverageType coverageType,
                       Instant archivedAt, UUID archivedBy, int version, Instant createdAt) {
    public static TestCaseCoverage create(UUID projectId, UUID testCaseId, String targetType, UUID targetId, CoverageType coverageType) {
        return new TestCaseCoverage(UUID.randomUUID(), projectId, testCaseId, targetType, targetId, coverageType, null, null, 0, Instant.now());
    }
    public TestCaseCoverage archive(UUID actorId) {
        return new TestCaseCoverage(id, projectId, testCaseId, targetType, targetId, coverageType, Instant.now(), actorId, version, createdAt);
    }
}
