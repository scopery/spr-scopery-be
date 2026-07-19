package com.company.scopery.modules.raid.raidlink.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRaidLinkRequest(
        @NotBlank String linkType,
        @NotBlank String targetType,
        @NotNull UUID targetId
) {}
