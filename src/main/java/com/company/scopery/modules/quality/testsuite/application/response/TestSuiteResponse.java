package com.company.scopery.modules.quality.testsuite.application.response;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuite;
import java.time.Instant; import java.util.UUID;
public record TestSuiteResponse(UUID id, UUID projectId, UUID testPlanId, String name, String status, Integer sortOrder, Instant createdAt) {
    public static TestSuiteResponse from(TestSuite e) { return new TestSuiteResponse(e.id(), e.projectId(), e.testPlanId(), e.name(), e.status().name(), e.sortOrder(), e.createdAt()); }
}
