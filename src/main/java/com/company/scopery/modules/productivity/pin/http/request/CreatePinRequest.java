package com.company.scopery.modules.productivity.pin.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreatePinRequest(@NotBlank String scope, @NotBlank String targetType, @NotNull UUID targetId, UUID projectId, Integer sortOrder) {}
