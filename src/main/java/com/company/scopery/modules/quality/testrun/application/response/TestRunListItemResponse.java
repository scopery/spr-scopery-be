package com.company.scopery.modules.quality.testrun.application.response;
import java.time.Instant; import java.util.UUID;
public record TestRunListItemResponse(UUID id, UUID projectId, String name, String runType, String runScope, String status,
        long total, long executed, long passed, long failed, long blocked, long skipped,
        Instant startedAt, Instant completedAt, Instant createdAt,
        String releasePackageName, String deploymentEnvironmentName) {}
