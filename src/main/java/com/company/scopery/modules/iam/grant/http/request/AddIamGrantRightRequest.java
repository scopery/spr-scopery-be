package com.company.scopery.modules.iam.grant.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to attach a legacy right to an existing IAM access grant")
public record AddIamGrantRightRequest(
        @Schema(description = "ID of the right to attach to the grant", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID rightId) {
}
