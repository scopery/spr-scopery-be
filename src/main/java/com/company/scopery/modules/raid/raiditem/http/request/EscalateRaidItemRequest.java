package com.company.scopery.modules.raid.raiditem.http.request;

import jakarta.validation.constraints.NotBlank;

public record EscalateRaidItemRequest(
        @NotBlank String escalationLevel,
        @NotBlank String reason
) {}
