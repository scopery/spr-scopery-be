package com.company.scopery.modules.iam.authorization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Batch request to evaluate multiple authorization checks in a single call (max 100 checks)")
public record CheckAuthorizationBatchRequest(
        @Schema(description = "List of individual authorization checks to evaluate (1–100 items)")
        @NotEmpty @Size(max = 100) List<@Valid CheckAuthorizationRequest> checks) {
}
