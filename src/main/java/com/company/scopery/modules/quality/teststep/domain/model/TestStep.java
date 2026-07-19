package com.company.scopery.modules.quality.teststep.domain.model;
import java.time.Instant; import java.util.UUID;
public record TestStep(UUID id, UUID projectId, UUID testCaseId, int stepOrder, String actionText, String expectedResult, String dataNotes, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static TestStep create(UUID projectId, UUID testCaseId, int stepOrder, String actionText, String expectedResult, String dataNotes) {
        Instant now = Instant.now();
        return new TestStep(UUID.randomUUID(), projectId, testCaseId, stepOrder, actionText, expectedResult, dataNotes, null, null, 0, now, now);
    }
    public TestStep archive(UUID actorId) {
        return new TestStep(id, projectId, testCaseId, stepOrder, actionText, expectedResult, dataNotes, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
