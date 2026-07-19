package com.company.scopery.modules.project.scheduling.schedulingissue.domain.model;

import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueSeverity;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SchedulingIssue(
        UUID id, UUID scheduleRunId, UUID projectId, UUID taskId, UUID userId, UUID workspaceMemberId,
        SchedulingIssueType issueType, SchedulingIssueSeverity severity, String message,
        LocalDate issueDate, String detailsJson, Instant createdAt, Instant updatedAt) {

    public static SchedulingIssue create(UUID runId, UUID projectId, UUID taskId, UUID userId, UUID memberId,
                                         SchedulingIssueType type, SchedulingIssueSeverity severity, String message,
                                         LocalDate issueDate) {
        return new SchedulingIssue(UUID.randomUUID(), runId, projectId, taskId, userId, memberId,
                type, severity, message, issueDate, null, null, null);
    }
}
