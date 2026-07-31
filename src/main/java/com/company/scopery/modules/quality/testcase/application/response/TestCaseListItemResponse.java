package com.company.scopery.modules.quality.testcase.application.response;
import java.time.Instant; import java.util.UUID;
public record TestCaseListItemResponse(
        UUID id, UUID projectId, String code, String title,
        String type, String priority, String status,
        AssigneeRef assignee, String automationStatus,
        long stepCount, long requirementCount, long useCaseCount,
        String latestResult, Instant latestResultAt, long openDefectCount,
        Instant createdAt, Instant updatedAt, Long version, UUID useCaseId) {
    public record AssigneeRef(UUID id, String displayName) {}
}
