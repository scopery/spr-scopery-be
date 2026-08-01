package com.company.scopery.platform.bulkjob;

import io.swagger.v3.oas.annotations.media.Schema;

public record BulkJobFailure(
        @Schema(description = "0-based index in the original request items array")
        int index,

        @Schema(description = "Best-effort business identity (key, code, or title). Null if not extractable.")
        String identity,

        @Schema(allowableValues = {"DUPLICATE_KEY", "NOT_FOUND", "VALIDATION", "FORBIDDEN", "UNKNOWN"})
        String errorCode,

        @Schema(description = "Human-readable failure reason (truncated to 500 chars)")
        String message,

        @Schema(description = "Original item payload — same shape as the create request body items, re-submittable to the same POST .../bulk endpoint")
        Object item
) {}
