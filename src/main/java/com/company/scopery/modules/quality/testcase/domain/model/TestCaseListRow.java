package com.company.scopery.modules.quality.testcase.domain.model;
import java.time.Instant;
import java.util.UUID;
public record TestCaseListRow(
        UUID id, UUID projectId, String code, String title, String type, String priority, String status,
        UUID assigneeId, String assigneeDisplayName, String automationStatus, long version,
        Instant createdAt, Instant updatedAt,
        long stepCount, long reqCount, long ucCount,
        String latestResult, Instant latestResultAt, UUID useCaseId) {}
