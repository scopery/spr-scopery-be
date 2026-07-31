package com.company.scopery.modules.quality.testcase.application.response;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record TestCaseDetailResponse(
        UUID id, UUID projectId, String code, String title,
        String description, String type, String priority, String status,
        UUID assigneeId, String automationStatus,
        String preconditions, String expectedResult,
        long stepCount, long requirementCount, long useCaseCount,
        String latestResult, Instant latestResultAt, long openDefectCount,
        List<Object> steps,
        Instant createdAt, Instant updatedAt, Long version, UUID useCaseId) {}
