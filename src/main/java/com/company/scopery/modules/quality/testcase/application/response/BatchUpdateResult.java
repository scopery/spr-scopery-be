package com.company.scopery.modules.quality.testcase.application.response;
import java.util.List; import java.util.UUID;
public record BatchUpdateResult(List<UUID> updated, List<BatchFailure> failed) {
    public record BatchFailure(UUID id, String reason) {}
}
