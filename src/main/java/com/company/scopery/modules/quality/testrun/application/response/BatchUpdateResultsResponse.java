package com.company.scopery.modules.quality.testrun.application.response;
import java.util.List; import java.util.UUID;
public record BatchUpdateResultsResponse(List<UUID> updated, List<ResultFailure> failed) {
    public record ResultFailure(UUID id, String reason) {}
}
