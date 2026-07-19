package com.company.scopery.modules.quality.testrun.application.response;
import com.company.scopery.modules.quality.testrun.domain.model.TestRun; import java.time.Instant; import java.util.UUID;
public record TestRunResponse(UUID id, UUID projectId, String name, String runType, String status, Instant startedAt, Instant completedAt, Instant createdAt) {
    public static TestRunResponse from(TestRun e) { return new TestRunResponse(e.id(), e.projectId(), e.name(), e.runType().name(), e.status().name(), e.startedAt(), e.completedAt(), e.createdAt()); }
}
