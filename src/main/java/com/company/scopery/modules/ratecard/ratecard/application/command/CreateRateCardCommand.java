package com.company.scopery.modules.ratecard.ratecard.application.command;
import java.util.UUID;
public record CreateRateCardCommand(String code, String name, String description, String scope,
        UUID organizationId, UUID workspaceId, UUID clientId, UUID projectId,
        String defaultCurrencyCode, Boolean isDefault) {}
