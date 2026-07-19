package com.company.scopery.modules.ratecard.costrole.application.command;

import java.util.UUID;

public record UpdateCostRoleCommand(UUID id, String name, String description, String category) {}
