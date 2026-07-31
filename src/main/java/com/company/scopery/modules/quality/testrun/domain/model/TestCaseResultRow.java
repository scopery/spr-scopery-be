package com.company.scopery.modules.quality.testrun.domain.model;
import java.time.Instant;
import java.util.UUID;
public record TestCaseResultRow(
        UUID id, UUID testRunId,
        UUID tcId, String tcCode, String tcTitle,
        String resultStatus, UUID defectId, String comment, UUID assigneeId,
        Instant executedAt, Instant createdAt, long version) {}
