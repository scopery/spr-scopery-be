package com.company.scopery.modules.traceability.tracelink.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateTraceLinkRequest(@NotBlank String sourceType, @NotNull UUID sourceId, @NotBlank String targetType, @NotNull UUID targetId, @NotBlank String linkType) {}
