package com.company.scopery.modules.iam.ownerpolicy.application.response;

import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Owner policy that automatically grants access to the owner of a newly created resource")
public record IamOwnerPolicyResponse(
        @Schema(description = "Unique identifier of the owner policy", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Resource type this policy applies to (e.g. AI_AGENT, WORKSPACE)", example = "AI_AGENT")
        String resourceType,

        @Schema(description = "Monotonically increasing version of the policy definition", example = "1")
        int policyVersion,

        @Schema(description = "Current status of the policy", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "List of permission-action pairs granted to the resource owner")
        List<IamOwnerPolicyAction> actions,

        @Schema(description = "Inheritance scope controlling how the policy propagates to child resources", example = "DIRECT")
        String inheritanceScope,

        @Schema(description = "Whether owners granted by this policy can delegate their access to others", example = "true")
        boolean canDelegate,

        @Schema(description = "Maximum delegation depth allowed when canDelegate is true (0 = no further delegation)", example = "1")
        int delegationDepth,

        @Schema(description = "Timestamp from which this policy version becomes effective", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant effectiveFrom,

        @Schema(description = "Timestamp at which this policy version expires (null = no expiry)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant effectiveTo,

        @Schema(description = "Optimistic-lock version counter for this policy record", example = "1")
        int version) {

    public static IamOwnerPolicyResponse from(IamOwnerPolicy policy) {
        return new IamOwnerPolicyResponse(policy.id(), policy.resourceType().name(), policy.policyVersion(),
                policy.status().name(), policy.actionBundle(), policy.inheritanceScope().name(),
                policy.canDelegate(), policy.delegationDepth(), policy.effectiveFrom(),
                policy.effectiveTo(), policy.version());
    }
}
