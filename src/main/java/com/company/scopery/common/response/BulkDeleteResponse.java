package com.company.scopery.common.response;

import java.util.List;
import java.util.UUID;

public record BulkDeleteResponse(
        int totalRequested,
        int succeededCount,
        int failedCount,
        List<UUID> succeeded,
        List<BulkDeleteResponse.Failure> failures
) {
    public record Failure(UUID id, String errorCode, String message) {}

    public static BulkDeleteResponse of(int total, List<UUID> succeeded, List<Failure> failures) {
        return new BulkDeleteResponse(total, succeeded.size(), failures.size(), succeeded, failures);
    }
}
