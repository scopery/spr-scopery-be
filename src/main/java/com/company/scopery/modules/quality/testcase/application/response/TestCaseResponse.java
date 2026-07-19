package com.company.scopery.modules.quality.testcase.application.response;
import com.company.scopery.modules.quality.testcase.domain.model.TestCase; import java.time.Instant; import java.util.UUID;
public record TestCaseResponse(UUID id, UUID projectId, String code, String title, String type, String priority, String status, Instant createdAt) {
    public static TestCaseResponse from(TestCase e) { return new TestCaseResponse(e.id(), e.projectId(), e.code(), e.title(), e.type().name(), e.priority().name(), e.status().name(), e.createdAt()); }
}
