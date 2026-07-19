package com.company.scopery.modules.quality.testrun.domain.model;
import com.company.scopery.modules.quality.testrun.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record TestRun(UUID id, UUID projectId, UUID workspaceId, UUID testPlanId, UUID testSuiteId, UUID releasePackageId,
        UUID deploymentEnvironmentId, String name, TestRunType runType, TestRunStatus status, Instant startedAt,
        Instant completedAt, UUID executedBy, String summaryJson, Instant archivedAt, UUID archivedBy, String traceId,
        int version, Instant createdAt, Instant updatedAt) {
    public static TestRun create(UUID projectId, UUID workspaceId, String name, TestRunType type, UUID testPlanId, UUID testSuiteId, UUID releasePackageId) {
        Instant now = Instant.now();
        return new TestRun(UUID.randomUUID(), projectId, workspaceId, testPlanId, testSuiteId, releasePackageId, null, name, type,
                TestRunStatus.PLANNED, null, null, null, null, null, null, null, 0, now, now);
    }
    public TestRun start(UUID actorId) {
        if (status != TestRunStatus.PLANNED) throw new IllegalStateException("must be planned");
        return new TestRun(id, projectId, workspaceId, testPlanId, testSuiteId, releasePackageId, deploymentEnvironmentId, name, runType,
                TestRunStatus.IN_PROGRESS, Instant.now(), null, actorId, summaryJson, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public TestRun complete(String summary) {
        if (status != TestRunStatus.IN_PROGRESS) throw new IllegalStateException("must be in progress");
        return new TestRun(id, projectId, workspaceId, testPlanId, testSuiteId, releasePackageId, deploymentEnvironmentId, name, runType,
                TestRunStatus.COMPLETED, startedAt, Instant.now(), executedBy, summary, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public TestRun cancel() {
        return new TestRun(id, projectId, workspaceId, testPlanId, testSuiteId, releasePackageId, deploymentEnvironmentId, name, runType,
                TestRunStatus.CANCELLED, startedAt, Instant.now(), executedBy, summaryJson, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
}
