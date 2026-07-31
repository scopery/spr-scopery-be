package com.company.scopery.modules.quality.testrun.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record BatchUpdateResultsRequest(@NotEmpty List<UUID> resultIds, ResultChanges changes) {
    public record ResultChanges(String result, UUID assigneeId) {}
}
