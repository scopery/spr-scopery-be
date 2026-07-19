package com.company.scopery.modules.raid.raidaction.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateRaidActionRequest(
        @NotBlank String title,
        String description,
        UUID ownerUserId,
        LocalDate dueDate
) {}
