package com.company.scopery.modules.quality.teststep.domain.model;
import java.time.Instant; import java.util.UUID;
public record TestCaseStep(UUID id, UUID testCaseId, UUID projectId, int sortOrder, String action, String expectedResult,
        UUID screenId, UUID componentId, Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static TestCaseStep create(UUID testCaseId, UUID projectId, int sortOrder, String action,
                                      String expectedResult, UUID screenId, UUID componentId) {
        Instant now = Instant.now();
        return new TestCaseStep(UUID.randomUUID(), testCaseId, projectId, sortOrder, action, expectedResult,
                screenId, componentId, null, null, -1, now, now);
    }
    public TestCaseStep update(String action, String expectedResult, UUID screenId, UUID componentId) {
        return new TestCaseStep(id, testCaseId, projectId, sortOrder,
                action != null ? action : this.action,
                expectedResult != null ? expectedResult : this.expectedResult,
                screenId, componentId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public TestCaseStep withSortOrder(int newOrder) {
        return new TestCaseStep(id, testCaseId, projectId, newOrder, action, expectedResult,
                screenId, componentId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public TestCaseStep archive(UUID actorId) {
        return new TestCaseStep(id, testCaseId, projectId, sortOrder, action, expectedResult,
                screenId, componentId, Instant.now(), actorId, version, createdAt, Instant.now());
    }
    public boolean isArchived() { return archivedAt != null; }
}
