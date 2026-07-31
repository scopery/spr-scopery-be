package com.company.scopery.modules.quality.testcase.application.response;
import com.company.scopery.modules.quality.testcase.domain.model.TestCase; import java.time.Instant; import java.util.UUID;
public record TestCaseResponse(UUID id, UUID projectId, String code, String title, String description,
        String type, String priority, String status, UUID useCaseId, UUID assigneeId, String automationStatus,
        String preconditions, String expectedResult, Instant createdAt, Instant updatedAt, Long version) {
    public static TestCaseResponse from(TestCase e) {
        return new TestCaseResponse(e.id(), e.projectId(), e.code(), e.title(), e.description(),
                e.type().name(), e.priority().name(), e.status().name(), e.useCaseId(), e.assigneeId(),
                e.automationStatus() != null ? e.automationStatus().name() : "MANUAL",
                e.preconditions(), e.expectedResult(), e.createdAt(), e.updatedAt(), (long) e.version());
    }
}
