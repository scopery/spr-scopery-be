package com.company.scopery.modules.quality.teststep.application.response;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import java.time.Instant; import java.util.UUID;
public record TestCaseStepResponse(UUID id, UUID testCaseId, UUID projectId, int sortOrder, String action,
        String expectedResult, UUID screenId, UUID componentId, Instant archivedAt,
        Instant createdAt, Instant updatedAt, Long version) {
    public static TestCaseStepResponse from(TestCaseStep e) {
        return new TestCaseStepResponse(e.id(), e.testCaseId(), e.projectId(), e.sortOrder(), e.action(),
                e.expectedResult(), e.screenId(), e.componentId(), e.archivedAt(),
                e.createdAt(), e.updatedAt(), (long) e.version());
    }
}
