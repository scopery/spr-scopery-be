package com.company.scopery.modules.quality.testcase.domain.model;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record TestCase(UUID id, UUID projectId, UUID testSuiteId, UUID useCaseId, String code, String title, String description,
        TestCaseType type, TestCasePriority priority, TestCaseStatus status, String preconditions, String expectedResult,
        int versionNumber, Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy,
        UUID assigneeId, AutomationStatus automationStatus,
        int version, Instant createdAt, Instant updatedAt) {
    public static TestCase create(UUID projectId, UUID testSuiteId, UUID useCaseId, String code, String title, String description,
                                  TestCaseType type, TestCasePriority priority, String preconditions, String expectedResult,
                                  UUID assigneeId, AutomationStatus automationStatus) {
        Instant now = Instant.now();
        return new TestCase(UUID.randomUUID(), projectId, testSuiteId, useCaseId, code, title, description, type, priority,
                TestCaseStatus.DRAFT, preconditions, expectedResult, 1, null, null, null, null,
                assigneeId, automationStatus != null ? automationStatus : AutomationStatus.MANUAL,
                -1, now, now);
    }
    public boolean isEditable() { return status == TestCaseStatus.DRAFT || status == TestCaseStatus.READY; }
    public TestCase approve(UUID actorId) {
        if (!isEditable() && status != TestCaseStatus.READY) throw new IllegalStateException("immutable");
        return new TestCase(id, projectId, testSuiteId, useCaseId, code, title, description, type, priority, TestCaseStatus.APPROVED,
                preconditions, expectedResult, versionNumber, Instant.now(), actorId, archivedAt, archivedBy,
                assigneeId, automationStatus, version, createdAt, Instant.now());
    }
    public TestCase archive(UUID actorId) {
        return new TestCase(id, projectId, testSuiteId, useCaseId, code, title, description, type, priority, TestCaseStatus.ARCHIVED,
                preconditions, expectedResult, versionNumber, approvedAt, approvedBy, Instant.now(), actorId,
                assigneeId, automationStatus, version, createdAt, Instant.now());
    }
    public TestCase update(String title, String description, TestCaseType type, TestCasePriority priority,
                           TestCaseStatus status, String preconditions, String expectedResult,
                           UUID useCaseId, UUID assigneeId, AutomationStatus automationStatus) {
        return new TestCase(id, projectId, testSuiteId, useCaseId != null ? useCaseId : this.useCaseId,
                code, title, description, type, priority,
                status != null ? status : this.status, preconditions, expectedResult,
                versionNumber, approvedAt, approvedBy, archivedAt, archivedBy,
                assigneeId, automationStatus != null ? automationStatus : this.automationStatus,
                version, createdAt, Instant.now());
    }
}
