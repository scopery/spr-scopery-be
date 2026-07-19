package com.company.scopery.modules.governance.ownership.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record AssignOwnershipRequest(@NotBlank String objectTypeCode, @NotNull UUID targetId, @NotBlank String ownerTargetType, @NotNull UUID ownerTargetId) {}
