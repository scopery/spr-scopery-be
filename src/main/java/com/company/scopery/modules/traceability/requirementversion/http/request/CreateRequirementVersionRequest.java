package com.company.scopery.modules.traceability.requirementversion.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateRequirementVersionRequest(@NotBlank String title, String description, String changeSummary, UUID createdByUserId, @NotNull UUID workspaceId) {}
