package com.company.scopery.modules.quality.testcase.domain.model;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record TestCase(UUID id, UUID projectId, UUID testSuiteId, String code, String title, String description,
        TestCaseType type, TestCasePriority priority, TestCaseStatus status, String preconditions, String expectedResult,
        int versionNumber, Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static TestCase create(UUID projectId, UUID testSuiteId, String code, String title, String description,
                                  TestCaseType type, TestCasePriority priority, String preconditions, String expectedResult) {
        Instant now = Instant.now();
        return new TestCase(UUID.randomUUID(), projectId, testSuiteId, code, title, description, type, priority,
                TestCaseStatus.DRAFT, preconditions, expectedResult, 1, null, null, null, null, 0, now, now);
    }
    public boolean isEditable() { return status == TestCaseStatus.DRAFT || status == TestCaseStatus.READY; }
    public TestCase approve(UUID actorId) {
        if (!isEditable() && status != TestCaseStatus.READY) throw new IllegalStateException("immutable");
        return new TestCase(id, projectId, testSuiteId, code, title, description, type, priority, TestCaseStatus.APPROVED,
                preconditions, expectedResult, versionNumber, Instant.now(), actorId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public TestCase archive(UUID actorId) {
        return new TestCase(id, projectId, testSuiteId, code, title, description, type, priority, TestCaseStatus.ARCHIVED,
                preconditions, expectedResult, versionNumber, approvedAt, approvedBy, Instant.now(), actorId, version, createdAt, Instant.now());
    }
    public TestCase update(String title, String description, TestCaseType type, TestCasePriority priority, String preconditions, String expectedResult) {
        if (!isEditable()) throw new IllegalStateException("immutable");
        return new TestCase(id, projectId, testSuiteId, code, title, description, type, priority, status, preconditions, expectedResult,
                versionNumber, approvedAt, approvedBy, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
