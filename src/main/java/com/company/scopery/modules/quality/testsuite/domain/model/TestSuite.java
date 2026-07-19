package com.company.scopery.modules.quality.testsuite.domain.model;
import com.company.scopery.modules.quality.testsuite.domain.enums.TestSuiteStatus;
import java.time.Instant; import java.util.UUID;
public record TestSuite(UUID id, UUID projectId, UUID testPlanId, UUID deliverableId, UUID scopeItemId, String name, String description, TestSuiteStatus status, Integer sortOrder, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static TestSuite create(UUID projectId, UUID testPlanId, String name, String description, UUID deliverableId, UUID scopeItemId, Integer sortOrder) {
        Instant now = Instant.now();
        return new TestSuite(UUID.randomUUID(), projectId, testPlanId, deliverableId, scopeItemId, name, description, TestSuiteStatus.ACTIVE, sortOrder, null, null, 0, now, now);
    }
    public TestSuite archive(UUID actorId) {
        return new TestSuite(id, projectId, testPlanId, deliverableId, scopeItemId, name, description, TestSuiteStatus.ARCHIVED, sortOrder, Instant.now(), actorId, version, createdAt, Instant.now());
    }
    public TestSuite update(String name, String description, Integer sortOrder) {
        return new TestSuite(id, projectId, testPlanId, deliverableId, scopeItemId, name, description, status, sortOrder, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
