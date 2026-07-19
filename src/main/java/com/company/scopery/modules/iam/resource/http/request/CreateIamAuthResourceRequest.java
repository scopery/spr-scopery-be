package com.company.scopery.modules.iam.resource.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to register a new IAM authorization resource that access control can be enforced against")
public record CreateIamAuthResourceRequest(
        @Schema(description = "Unique machine-readable code for this resource (2–100 characters)", example = "AI_AGENT_SALES_BOT")
        @NotBlank @Size(min = 2, max = 100) String code,

        @Schema(description = "Resource type categorizing this entity (e.g. AI_AGENT, WORKSPACE, PROJECT)", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Human-readable display name of the resource (max 255 characters)", example = "Sales Bot Agent")
        @NotBlank @Size(max = 255) String name,

        @Schema(description = "Optional description providing context for this resource (max 2000 characters)", example = "AI agent used by the sales team for lead qualification", nullable = true)
        @Size(max = 2000) String description) {}
