package com.company.scopery.modules.iam.ownerpolicy.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request to create an owner policy that automatically grants access to whoever creates a resource of the specified type")
public record CreateIamOwnerPolicyRequest(
        @Schema(description = "Resource type this policy applies to (e.g. AI_AGENT, WORKSPACE)", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Inheritance scope controlling how the auto-grant propagates to child resources (e.g. DIRECT, RECURSIVE)", example = "DIRECT")
        @NotBlank String inheritanceScope,

        @Schema(description = "Whether the resource owner can delegate their auto-granted access to other subjects", example = "true")
        boolean canDelegate,

        @Schema(description = "Maximum delegation hops allowed when canDelegate is true (0 = no further delegation)", example = "1")
        @Min(0) int delegationDepth,

        @Schema(description = "List of permission-action pairs to automatically grant to the resource owner (at least one required)")
        @NotEmpty List<@Valid ActionRequest> actions) {

    @Schema(description = "A single permission + action pair included in the owner policy")
    public record ActionRequest(
            @Schema(description = "Permission code (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
            @NotBlank String permissionCode,

            @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
            @NotBlank String actionCode) {}
}
