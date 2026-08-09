package com.company.scopery.modules.profitability.profile.application.command;

import java.util.UUID;

public record CreateProfitabilityProfileCommand(
        UUID projectId,
        UUID workspaceId,
        String currency
) {}
