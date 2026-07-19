package com.company.scopery.modules.raid.raiditem.http.request;

import jakarta.validation.constraints.NotBlank;

public record ResolveRaidItemRequest(@NotBlank String outcomeNote) {}
