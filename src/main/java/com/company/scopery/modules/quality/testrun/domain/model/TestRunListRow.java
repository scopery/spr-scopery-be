package com.company.scopery.modules.quality.testrun.domain.model;
import java.time.Instant;
import java.util.UUID;
public record TestRunListRow(
        UUID id, UUID projectId, String name, String runType, String runScope, String status,
        long total, long executed, long passed, long failed, long blocked, long skipped,
        Instant startedAt, Instant completedAt, Instant createdAt,
        String releasePackageName, String deploymentEnvironmentName) {}
