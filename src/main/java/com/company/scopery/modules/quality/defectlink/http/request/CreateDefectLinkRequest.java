package com.company.scopery.modules.quality.defectlink.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateDefectLinkRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String linkType) {}
