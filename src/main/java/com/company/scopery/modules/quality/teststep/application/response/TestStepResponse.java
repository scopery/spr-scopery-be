package com.company.scopery.modules.quality.teststep.application.response;
import com.company.scopery.modules.quality.teststep.domain.model.TestStep;
import java.time.Instant; import java.util.UUID;
public record TestStepResponse(UUID id, UUID projectId, UUID testCaseId, int stepOrder, String actionText, String expectedResult, Instant createdAt) {
    public static TestStepResponse from(TestStep e) { return new TestStepResponse(e.id(), e.projectId(), e.testCaseId(), e.stepOrder(), e.actionText(), e.expectedResult(), e.createdAt()); }
}
