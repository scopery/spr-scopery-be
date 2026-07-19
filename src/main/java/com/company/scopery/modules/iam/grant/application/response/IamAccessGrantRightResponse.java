package com.company.scopery.modules.iam.grant.application.response;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A legacy right entry attached to an IAM access grant for backward-compatibility")
public record IamAccessGrantRightResponse(
        @Schema(description = "ID of the parent access grant", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID grantId,

        @Schema(description = "ID of the right associated with this grant", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID rightId,

        @Schema(description = "Timestamp when this right entry was attached to the grant", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static IamAccessGrantRightResponse from(IamAccessGrantRight domain) {
        return new IamAccessGrantRightResponse(domain.grantId(), domain.rightId(), domain.createdAt());
    }
}
