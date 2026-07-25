package com.company.scopery.modules.traceability.tracelink.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record BatchCreateTraceLinkRequest(@NotEmpty @Valid List<LinkDto> links) {
    public record LinkDto(
            @NotBlank String sourceType,
            UUID sourceId,
            @NotBlank String targetType,
            UUID targetId,
            @NotBlank String linkType
    ) {}
}
